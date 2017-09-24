/*
 * Trace.java
 *
 * Copyright (c) 2002-2015 Alexei Drummond, Andrew Rambaut and Marc Suchard
 *
 * This file is part of BEAST.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership and licensing.
 *
 * BEAST is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *  BEAST is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with BEAST; if not, write to the
 * Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package dr.inference.trace;

import java.util.*;

/**
 * A simple class that stores a trace for a single statistic
 *
 * @author Andrew Rambaut
 * @author Alexei Drummond
 * @version $Id: Trace.java,v 1.11 2005/07/11 14:07:26 rambaut Exp $
 */
public class Trace {
    public static final int MAX_UNIQUE_VALUES = 100; // the maximum allowed number of unique values

    protected TraceType traceType = TraceType.REAL;
    protected List<Double> values = new ArrayList<Double>();
    protected String name;

    List<String> categoryValueList = new ArrayList<String>();
    Map<Integer, String> categoryLabelMap = new TreeMap<Integer, String>();
    Map<Integer, Integer> categoryOrderMap = new TreeMap<Integer, Integer>();
    Set<Integer> uniqueValues = new HashSet<Integer>();

    public Trace(String name) { // traceType = TraceFactory.TraceType.DOUBLE; 
        this.name = name;
    }

    public Trace(String name, TraceType traceType) {
        this.name = name;
        setTraceType(traceType);
    }

    /**
     * @param value the valued to be added
     */
    public void add(Double value) {
        if (uniqueValues.size() < MAX_UNIQUE_VALUES) {
            // unique values are treated as integers
            uniqueValues.add(value.intValue());
        }

        values.add(value);
    }

    /**
     * @param valuesArray the values to be added
     */
    public void add(Double[] valuesArray) {
        for (Double value : valuesArray) {
            add(value);
        }
    }

    /**
     * @param value the valued to be added
     */
    public void add(Integer value) {
        if (uniqueValues.size() < MAX_UNIQUE_VALUES) {
            // unique values are treated as integers
            uniqueValues.add(value);
        }

        values.add(value.doubleValue());
    }

    /**
     * Add a categorical value
     * @param value the valued to be added
     */
    public void add(String value) {
        int index = categoryValueList.indexOf(value);
        if (index < 0) {
            categoryValueList.add(value);
            index = categoryValueList.size() - 1;
            categoryLabelMap.put(index, value);
            categoryOrderMap.put(index, index);
        }
        add(index);
    }

    /**
     * @param valuesArray the values to be added
     */
    public void add(String[] valuesArray) {
        for (String value : valuesArray) {
            add(value);
        }
    }

    public Map<Integer, Integer> getCategoryOrderMap() {
        return categoryOrderMap;
    }

    public void setCategoryOrderMap(Map<Integer, Integer> categoryOrderMap) {
        this.categoryOrderMap.clear();
        this.categoryOrderMap.putAll(categoryOrderMap);
    }

    public int getValueCount() {
        return values.size();
    }

    public int getUniqueValueCount() {
        return uniqueValues.size();
    }

    public double getValue(int index) {
        return values.get(index);
    }

    public int getCategory(int index) {
        return categoryOrderMap.get(values.get(index).intValue());
    }

    public String getCategoryLabel(int index) {
        return categoryLabelMap.get(getCategory(index));
    }

    public Map<Integer, String> getCategoryLabelMap() {
        return categoryLabelMap;
    }

    public double[] getRange() { // Double => bounds; Integer and String => unique values

        if (getValueCount() < 1) throw new IllegalArgumentException("Cannot find values in trace " + getName());

        if (getTraceType().isNumber()) {

            Double min = Double.MAX_VALUE;
            Double max = Double.MIN_VALUE;
            for (Double value : values) {
                if ( value < min) {
                    min = value;
                } else if (value > max) {
                    max = value;
                }
            }
            return new double[] {min, max};
        } else {
            throw new UnsupportedOperationException("cannot call getRange for categorical data");
        }
    }

    /**
     * @param fromIndex low endpoint (inclusive) of the subList.
     * @param toIndex   high endpoint (exclusive) of the subList.
     * @return The list of values (which are selected values if filter applied)
     */
    public List<Double> getValues(int fromIndex, int toIndex) {
        return getValues(fromIndex, toIndex, null);
    }

    public List<Double> getValues(int fromIndex, int toIndex, boolean[] filtered) {
        if (toIndex > getValueCount() || fromIndex > toIndex) {
            throw new RuntimeException("Invalid index : fromIndex = " + fromIndex + "; toIndex = " + toIndex
                    + "; List size = " + getValueCount() + "; in Trace " + name);
        }

        if (filtered == null || filtered.length < 1) {
            return values.subList(fromIndex, toIndex);
        } else {
            List<Double> valuesList = new ArrayList<Double>();
            for (int i = fromIndex; i < toIndex; i++) {
                if (!filtered[i])
                    valuesList.add(values.get(i));
            }
            if (valuesList.size() < 1)
                throw new RuntimeException("There is no value left after all filters are applied !");

            return valuesList;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public TraceType getTraceType() {
        return traceType;
    }

    public void setTraceType(TraceType traceType) {
        this.traceType = traceType;
    }

    //******************** TraceCorrelation ****************************
    protected TraceCorrelation traceStatistics;

    public TraceCorrelation getTraceStatistics() {
        return traceStatistics;
    }

    public void setTraceStatistics(TraceCorrelation traceStatistics) {
        this.traceStatistics = traceStatistics;
    }

    //******************** Filter ****************************
    protected Filter filter = null;

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }
}