package com.company;

import java.io.Serializable;
import java.util.Comparator;

public class DocumentRelevanceComparator implements Comparator<DocumentRelevance>, Serializable {
    @Override
    public int compare(DocumentRelevance o1, DocumentRelevance o2) {
        return Double.compare(o2.getRelevance(), o1.getRelevance());
    }
}
