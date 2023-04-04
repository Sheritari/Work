package com.company;

import java.io.Serializable;

public class DocumentRelevance implements Serializable {

    //Идентификатор документа
    private int docID;
    //Степень соответствия документа запросу
    private double relevance;

    /**
     * Конструктор класса
     * @param startDocID - идентификатор документа
     */
    public DocumentRelevance(int startDocID) {
        docID = startDocID;
    }

    /**
     * Метод для получения идентификатора документа
     * @return - возвращает идентификатор документа
     */
    public int getDocID() {
        return docID;
    }

    /**
     * Метод для получения меры соответствия документа запросу
     * @return - возвращает меру соответствия документа запросу
     */
    public double getRelevance() {
        return relevance;
    }

    /**
     * Метод для увеличения релевантности текущего документа на значение tf
     *
     * @param tf - 1 + log(tf)
     */
    public void updateRelevance(double tf) {
        relevance += tf;
    }

    /**
     * Метод для получения строкового представления экземпляра класса
     * @return - возвращает строковое представление экземпляра класса
     */
    public String toString() {
        return String.format("%d - %f", docID, relevance);
    }
}
