package com.company;

import java.io.*;
import java.util.Scanner;

public class TfIdf {

    public TfIdf(){}

    public void serialize(InvertedIndex index) {
        try {
            FileOutputStream fos = new FileOutputStream("collection.out");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(index);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public InvertedIndex deserialize() {
        InvertedIndex index = new InvertedIndex();
        try {
            FileInputStream fis = new FileInputStream("collection.out");
            ObjectInputStream oin = new ObjectInputStream(fis);
            index = (InvertedIndex) oin.readObject();
            return index;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return index;
    }

    public InvertedIndex getInvertedIndex(){
        File f = new File("collection.out");
        InvertedIndex index = new InvertedIndex();
        if (f.exists()) {
            index = deserialize();
        } else {
            index.indexCollection("collection");
            serialize(index);
        }
        return index;
    }

    public boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

//    public static void main(String[] args) {
//        InvertedIndex index = getInvertedIndex();
//        Scanner in = new Scanner(System.in);
//        while (true) {
//            String query = in.nextLine();
//            if (query.equals("Explosion")) {
//                break;
//            }
//            System.out.println();
//            String[] t = query.split(" ");
//            String str = t[t.length - 1];
//            if (isNumeric(str)) {
//                if (Integer.parseInt(str) <= 0) {
//                    System.out.println("Количество документов должно быть > 0\n");
//                    continue;
//                }
//                String trueQuery = query.substring(0, query.length() - 2);
//                System.out.println("Запрос: " + query + index.printResult(index.executeQuery(trueQuery, Integer.parseInt(str))));
//            } else {
//                System.out.println("Запрос: " + query + index.printResult(index.executeQuery(query)));
//            }
//        }
//    }
}
