package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.tartarus.snowball.ext.russianStemmer;

public class InvertedIndex implements Serializable {
    private final List<String> documents;
    private final Map<String, Term> index;
    private final russianStemmer stemmer;
    private int counter = 0;

    public void prt() {
        for (Map.Entry<String, Term> entry : index.entrySet()) {
            System.out.println(entry.getKey());
        }
    }

    private void printFlex() {
        for (int i = 0; i < 85; i++) {
            if (i == 0 || i == 9 || i == 72 || i == 84) {
                System.out.print("+");;
            } else {
                System.out.print("-");
            }
        }
        System.out.println();
    }

    private void printStart() {
        printFlex();
        System.out.print("| docID  | file");
        for (int i = 0; i < 57; i++) {
            System.out.print(" ");
        }
        System.out.println("| indexsize |");
        printFlex();
    }

    public void printAnswer(File f) {
        int fSize = f.getParentFile().getName().length() + 1 + f.getName().length();
        if (counter <= 9) {
            System.out.print("|     ");
        } else {
            System.out.print("|    ");
        }
        System.out.print(counter++ + "  | " + f.getParentFile().getName() + "\\" + f.getName() + " ");
        for (int i = 0; i < 60 - fSize; i++) {
            System.out.print(" ");
        }
        if (index.size() <= 9999) {
            System.out.println("|     " + index.size() + "  |");
        } else {
            System.out.println("|    " + index.size() + "  |");
        }
    }

    public InvertedIndex() {
        documents = new LinkedList<>();
        index = new HashMap<>();
        stemmer = new russianStemmer();
    }

    public void indexTXT(String path) {
        File f = new File(path);
        try {
            Scanner in = new Scanner(f);
            StringBuilder text = new StringBuilder();
            while(in.hasNext()) {
                text.append(" ").append(in.next());
            }
            indexString(text.toString(), f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void indexWeb(String path) {
        try {
            Document doc = Jsoup.connect(path)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
            String text = doc.body().text();
            indexString(text, path);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void indexString(String text, String path) {
        Scanner in = new Scanner(text);
        if (!documents.contains(path)) {
            documents.add(path);
        }
        int CurrentDoc = documents.indexOf(path);
        while(in.hasNext()) {
            String s = in.next();
            s = s.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9_]","");
            if (s.equals("")) {
                continue;
            }
            s = s.toLowerCase();
            stemmer.setCurrent(s);
            stemmer.stem();
            s = stemmer.getCurrent();
            if (!index.containsKey(s)) {
                index.put(s, new Term(CurrentDoc));
            } else {
                index.get(s).addDocument(CurrentDoc);
            }
        }
    }

    public void indexHTML(String path) {
        File f = new File(path);
        try {
            Document doc = Jsoup.parse(f, "UTF-8");
            String text_html = doc.body().text();
            indexString(text_html, f.getAbsolutePath());
        } catch (IOException e) {
            System.out.println(e.getMessage());;
        }
    }

    public void indexDocument(String path) {
        if (path.startsWith("http")) {
            indexWeb(path);
        } else {
            try {
                File f = new File(path);
                String mime = Files.probeContentType(f.toPath());
                if (mime.equals("text/plain")) {
                    indexTXT(path);
                } else if (mime.equals("text/html")) {
                    indexHTML(path);
                } else {
                    System.out.println("Неподходящий тип документа");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void indexCollection(String path) {
        File f = new File(path);
        if (f.isDirectory()) {
            File[] folder = f.listFiles();
            printStart();
            for (File fileEntry : folder) {
                if (fileEntry.isDirectory()) {
                    counter = 0;
                    indexCollection(fileEntry.getAbsolutePath());
                } else {
                    indexDocument(fileEntry.getAbsolutePath());
                    printAnswer(fileEntry);
                }
            }
            printFlex();
            for (Map.Entry<String, Term> entry : index.entrySet()) {
                index.get(entry.getKey()).computeTfIdf(Math.log10((double) documents.size() / (double) index.get(entry.getKey()).getDocumentFrequency()));
            }
            System.out.println();
        } else {
            System.out.println("Неверный путь");
        }
    }

    void getIntersection(List<DocumentRelevance> answer, Term term) {
        List<TermDocument> list = term.getList();
        for (TermDocument element : list) {
            answer.get(element.getDocID()).updateRelevance(element.getTfIdf());
        }
    }

    public List<DocumentRelevance> executeQuery(String query) {
        String[] words;
        List<DocumentRelevance> answer = new ArrayList<>(documents.size());
        for (int i = 0; i < documents.size(); i++) {
            DocumentRelevance temp = new DocumentRelevance(i);
            answer.add(temp);
        }
        words = query.toLowerCase().split(" ");
        for (String i : words) {
            i = i.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9_]","");
            stemmer.setCurrent(i);
            stemmer.stem();
            Term temp = index.get(stemmer.getCurrent());
            if (temp != null) {
                getIntersection(answer, temp);
            } else {
                return new LinkedList<>();
            }
        }
        answer.removeIf(t -> t.getRelevance() == 0);
        answer.sort(new DocumentRelevanceComparator());
        return answer;
    }

    public List<DocumentRelevance> executeQuery(String query, int n) {
        List<DocumentRelevance> answer = executeQuery(query);
        return answer.subList(0, n);
    }

    public String printResult(List<DocumentRelevance> answer) {
        if (answer.size() == 0) {
            return "\nПо вашему запросу не найдено совпадений\n";
        }
        StringBuilder temp = new StringBuilder();
        temp.append("\n");
        for (DocumentRelevance documentRelevance : answer) {
            int docId = documentRelevance.getDocID();
            temp.append(docId).append(". (").append(documentRelevance.getRelevance()).append(") ").append(documents.get(docId)).append("\n");
        }
        return temp.toString();
    }

    public Map<String, Term> getIndex() {
        return index;
    }
}
