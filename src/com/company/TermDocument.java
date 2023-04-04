package com.company;

import java.io.Serializable;

public class TermDocument implements Serializable {

    //Идентификатор документа, в котором встретился терм
    private int docID;
    //Количество вхождений терма в документ
    private int tf;
    //Вес терма в документе
    private double tf_idf;

    /**
     * Конструктор класса, устанавливает начальную частоту
     * @param startDocID - идентификатор документа
     */
    public TermDocument(int startDocID) {
        docID = startDocID;
        tf = 1;
    }

    /**
     * Метод для получения идентификатора документа
     * @return - возвращает идентификатор документа
     */
    public int getDocID() {
        return docID;
    }

    /**
     * Метод для увеличения частоты терма для данного документа
     */
    public void increaseFrequency() {
        tf++;
    }

    /**
     * Метод для вычисления tf-idf
     * @param idf - обратная документная частота
     */
    public void computeTfIdf(double idf) {
        tf_idf = (1 + Math.log10(tf)) * idf;
    }

    /**
     * Метод для получения tf-idf
     * @return - возвращает tf-idf
     */
    public double getTfIdf() {
        return tf_idf;
    }

    /**
     * Метод для получения строкового представления экземпляра класса
     * @return - возвращает строковое представление экземпляра класса
     */
    public String toString() {
        return String.format("%d - docID\n%d - tf\n%f - tf-idf\n", docID, tf, tf_idf);
    }
}
