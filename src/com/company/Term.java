package com.company;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Term implements Serializable {

    //Список документов, в которых встретился терм
    private LinkedList<TermDocument> list;
    //Количество документов, в которые входит терм
    private int termFrequencyInCollection = 1;

    /**
     * Конструктор класса
     * @param docID - идентификатор документа, в котором встретился терм
     */
    public Term(int docID) {
        TermDocument term = new TermDocument(docID);
        list = new LinkedList<>();
        list.add(term);
    }

    /**
     * Метод для увеличения частоты терма в коллекции.
     * @param docID - идентификатор документа, в котором встретился терм
     */
    public void addDocument(int docID) {
        if (list.getLast().getDocID() == docID) {
            list.getLast().increaseFrequency();
        } else {
            TermDocument term = new TermDocument(docID);
            list.add(term);
            termFrequencyInCollection++;
        }
    }

    /**
     * Метод для вычисления tf-idf для всех объектов list
     * @param idf - обратная документная частота
     */
    public void computeTfIdf(double idf) {
        for (TermDocument term : list) {
            term.computeTfIdf(idf);
        }
    }

    /**
     * Метод для получения количества документов, в которые входит терм
     * @return - возвращает количество документов, в которые входит терм
     */
    public int getDocumentFrequency() {
        return termFrequencyInCollection;
    }

    /**
     * Метод для получения списка документов, в которых встретился терм
     * @return - возвращает список документов, в которых встретился терм
     */
    public List<TermDocument> getList() {
        return list;
    }
}
