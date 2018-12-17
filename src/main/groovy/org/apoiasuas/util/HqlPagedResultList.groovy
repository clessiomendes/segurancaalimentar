package org.apoiasuas.util

import grails.gorm.PagedResultList

/**
 * Classe para contornar a ausencia do recurso de paginacao em consultas HQL
 * Necessita que a lista de resultado (em um dado intervalo de paginacao) e o total de registros (sem paginacao) sejam alimentados explicitamente
 */
class HqlPagedResultList /*extends PagedResultList*/  {

    private List _resultList;
    private int _totalCount = Integer.MIN_VALUE;

    HqlPagedResultList(List resultList, int totalCount) {
        this._resultList = resultList
        this._totalCount = totalCount
    }

    public int getTotalCount() {
        return this._totalCount;
    }

    public List getResultList() {
        return this._resultList;
    }

}
