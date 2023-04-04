package com.company;

import com.aspose.cells.Worksheet;
import com.google.gson.*;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import com.aspose.cells.Workbook;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.russianStemmer;

public class Main {

    public static double getTfIdf(String word, Map<String, Term> terms) {
        return terms.get(word).getList().get(0).getTfIdf();
    }

    public static void main(String[] args) {

        String[] cellsTitles = {"text", "is_italic", "x_top_left", "font_size", "start", "is_bold", "font_name",
                "is_normal", "y_top_left", "width", "end", "height", "tf-idf"};

        SnowballStemmer stemmer = new russianStemmer();

        String encoding = System.getProperty("console.encoding", "utf-8");
        File folder = new File("files");
        File[] files = folder.listFiles();
        for (File f : files) {
            List<JsonObject> ans = new ArrayList<>();
            Workbook wb = new Workbook();
            Worksheet ws = wb.getWorksheets().get(0);
            JsonObject word;
            JsonArray annotations;
            JsonArray blocks;
            try {
                Scanner in = new Scanner(f, encoding);
                StringBuilder jsonLine = new StringBuilder();
                while (in.hasNextLine()) {
                    String s = in.nextLine();
                    jsonLine.append(s);
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonElement s = gson.fromJson(jsonLine.toString(), JsonElement.class);
                int pagesSize = s.getAsJsonObject().get("pages").getAsJsonArray().size();
                FileWriter fw = new FileWriter("collection\\" + f.getName().substring(0, f.getName().indexOf('.')) + ".txt");
                for (int i = 0; i < pagesSize; i++) {
                    blocks = s.getAsJsonObject().get("pages").getAsJsonArray().get(i)
                            .getAsJsonObject().get("blocks").getAsJsonArray();

                    for (int j = 0; j < blocks.size(); j++) {
                        annotations = blocks.get(j).getAsJsonObject().get("annotations").getAsJsonArray();
                        for (int k = 0; k < annotations.size(); k++) {
                            word = annotations.get(k).getAsJsonObject();
                            ans.add(word);
                            fw.write(word.get("text").getAsString() + " ");
                        }
                        fw.write("\n");
                    }

                }
                fw.flush();
                fw.close();
                Thread.sleep(100);
                TfIdf tfIdf = new TfIdf();

                InvertedIndex index = tfIdf.getInvertedIndex();
                System.out.println(index.executeQuery("оценка"));
                Map<String, Term> terms = index.getIndex();


                for (int i = 0; i < cellsTitles.length; i++) {
                    ws.getCells().get(0, i).putValue(cellsTitles[i]);
                }
                for (int i = 0; i < ans.size(); i++) {
                    for (int j = 0; j < cellsTitles.length; j++) {
                        if (j == cellsTitles.length - 1) {
                            String str = ans.get(i).get("text").getAsString();
                            str = str.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9_]","");
                            str = str.toLowerCase();
                            stemmer.setCurrent(str);
                            stemmer.stem();
                            str = stemmer.getCurrent();
                            try {
                                if (str.equals("")) {
                                    ws.getCells().get(i + 1, j).putValue(Double.toString(0.0));
                                    continue;
                                }
                                ws.getCells().get(i + 1, j).putValue(Double.toString(getTfIdf(str, terms)));
                            } catch (Exception e) {
//                                System.out.println(str);
                            }
                        } else {
                            ws.getCells().get(i + 1, j).putValue(ans.get(i).get(cellsTitles[j]).getAsString());
                        }
                    }
                }
                wb.save("csv\\" + f.getName().substring(0, f.getName().indexOf('.')) + ".csv");
            } catch (Exception e) {
            }
        }
    }
}
