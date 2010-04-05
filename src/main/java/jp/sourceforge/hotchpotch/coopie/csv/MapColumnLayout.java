package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

public class MapColumnLayout extends AbstractColumnLayout<Map<String, String>> {

    @Override
    protected ColumnDesc<Map<String, String>>[] getColumnDescs() {
        if (columnDescs != null) {
            return columnDescs;
        }

        if (columnNames != null) {
            final ColumnName[] names = columnNames.getColumnNames();
            final ColumnDesc<Map<String, String>>[] cds = newColumnDescs(names.length);
            int i = 0;
            for (final ColumnName columnName : names) {
                final ColumnDesc<Map<String, String>> cd = newMapColumnDesc(columnName);
                cds[i] = cd;
                i++;
            }
            orderSpecified = OrderSpecified.SPECIFIED;
            columnDescs = cds;
            return columnDescs;
        }

        return null;
    }

    /*
     * CSVを読むとき
     */
    @Override
    public void setupByHeader(final String[] header) {
        final ColumnDesc<Map<String, String>>[] tmpCds = getColumnDescs();
        if (tmpCds != null) {
            /*
             * columnNamesが設定されている場合は、ヘッダに合わせてソートし直す
             */
            super.setupByHeader(header);
            return;
        }

        /*
         * ヘッダをMapのキーとして扱う。
         */
        final ColumnDesc<Map<String, String>>[] cds = newColumnDescs(header.length);
        int i = 0;
        for (final String headerElem : header) {
            final ColumnName columnName = new SimpleColumnName(headerElem);
            final ColumnDesc<Map<String, String>> cd = newMapColumnDesc(columnName);
            cds[i] = cd;
            i++;
        }
        orderSpecified = OrderSpecified.SPECIFIED;
        columnDescs = cds;
    }

    private ColumnDesc<Map<String, String>> newMapColumnDesc(
        final ColumnName columnName) {
        final MapColumnDesc cd = new MapColumnDesc();
        cd.setName(columnName);
        return cd;
    }

    static class MapColumnDesc implements ColumnDesc<Map<String, String>> {

        /**
         * CSV列名。
         */
        private ColumnName name;

        @Override
        public ColumnName getName() {
            return name;
        }

        public void setName(final ColumnName name) {
            this.name = name;
        }

        @Override
        public String getValue(final Map<String, String> bean) {
            final String propertyName = name.getName();
            return bean.get(propertyName);
        }

        @Override
        public void setValue(final Map<String, String> bean, final String value) {
            final String propertyName = name.getName();
            bean.put(propertyName, value);
        }

    }

}
