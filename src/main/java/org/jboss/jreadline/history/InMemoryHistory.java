/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.jboss.jreadline.history;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple in-memory history implementation
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class InMemoryHistory implements History {

    private List<StringBuilder> historyList = new LinkedList<StringBuilder>();
    private int lastFetchedId = -1;
    private int lastSearchedId = 0;
    private StringBuilder current;
    private SearchDirection searchDirection = SearchDirection.REVERSE;

    @Override
    public void push(StringBuilder entry) {
        historyList.add(0, entry);
        lastFetchedId = -1;
        lastSearchedId = 0;
    }

    @Override
    public StringBuilder find(StringBuilder search) {
        int index = historyList.indexOf(search);
        if(index >= 0) {
            return get(index);
        }
        else
            return null;

    }

    @Override
    public StringBuilder get(int index) {
        lastFetchedId = index;
        return new StringBuilder(historyList.get(index));
    }

   @Override
    public int size() {
       return historyList.size();
   }

    @Override
    public void setSearchDirection(SearchDirection direction) {
        searchDirection = direction;
    }

    @Override
    public SearchDirection getSearchDirection() {
        return searchDirection;
    }

    @Override
    public StringBuilder getPreviousFetch() {
        if(size() < 1)
            return null;

        if(lastFetchedId < size()-1)
            return get(++lastFetchedId);
        else {
            return get(lastFetchedId);
        }
    }

    @Override
    public StringBuilder search(String search) {
        if(searchDirection == SearchDirection.REVERSE)
            return searchReverse(search);
        else
            return searchForward(search);
    }

    private StringBuilder searchForward(String search) {
        for(; lastSearchedId < size(); lastSearchedId++) {
            if(historyList.get(lastSearchedId).indexOf(search) != -1)
                return get(lastSearchedId);

        }

        return null;
    }

    private StringBuilder searchReverse(String search) {
        if(lastSearchedId < 1 || lastSearchedId >= size())
            lastSearchedId = size()-1;

        for(; lastSearchedId >= 0; lastSearchedId-- ) {
            if(historyList.get(lastSearchedId).indexOf(search) != -1)
                return get(lastSearchedId);
        }
        return null;
    }

    @Override
    public void setCurrent(StringBuilder line) {
        this.current = line;
    }

    @Override
    public StringBuilder getCurrent() {
        return current;
    }

    @Override
    public StringBuilder getNextFetch() {
        if(size() < 1)
            return null;

        if(lastFetchedId > 0)
            return get(--lastFetchedId);
        else {
            return getCurrent();
        }
    }

}