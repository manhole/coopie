package jp.sourceforge.hotchpotch.coopie.csv;

public abstract class AbstractColumnLayout<T> {

    protected ColumnDesc<T>[] columnDescs;
    // 一時的
    protected ColumnNames columnNames;

    public AbstractColumnLayout() {
    }

    public ColumnName[] getNames() {
        final ColumnDesc<T>[] cds = getColumnDescs();
        final ColumnName[] names = new ColumnName[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            names[i] = cd.getName();
        }
        return names;
    }

    public String[] getValues(final T bean) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        final String[] line = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            final String v = cd.getValue(bean);
            line[i] = v;
        }
        return line;
    }

    public void setValues(final T bean, final String[] line) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        for (int i = 0; i < line.length; i++) {
            final String elem = line[i];
            final ColumnDesc<T> cd = cds[i];
            cd.setValue(bean, elem);
        }
    }

    protected abstract ColumnDesc<T>[] getColumnDescs();

    public void setupColumns(final ColumnSetup columnSetup) {
        final ColumnNames columns = new ColumnNames();
        columns.setupColumns(columnSetup);
        setColumns(columns);
    }

    protected void setColumns(final String... names) {
        final ColumnName[] columns = new ColumnName[names.length];
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            final ColumnName columnName = new SimpleColumnName(name);
            columns[i] = columnName;
        }
        setColumns(columns);
    }

    protected void setColumns(final ColumnName... names) {
        final ColumnNames columnNames = new ColumnNames();
        for (final ColumnName name : names) {
            columnNames.add(name);
        }
        setColumns(columnNames);
    }

    public void setColumns(final ColumnNames columnNames) {
        this.columnNames = columnNames;
        columnDescs = null;
    }

    public void setupByHeader(final String[] header) {
        /*
         * ColumnDescをヘッダの順序に合わせてソートし直す。
         */
        final ColumnDesc<T>[] tmpCds = getColumnDescs();
        final ColumnDesc<T>[] cds = newColumnDescs(tmpCds.length);

        int i = 0;
        HEADER: for (final String headerElem : header) {
            for (final ColumnDesc<T> cd : tmpCds) {
                final ColumnName name = cd.getName();
                if (name.getLabel().equals(headerElem)) {
                    cds[i] = cd;
                    i++;
                    continue HEADER;
                }
            }
            // TODO
            throw new RuntimeException("headerElem=" + headerElem);
        }
        columnDescs = cds;
    }

    @SuppressWarnings("unchecked")
    protected ColumnDesc<T>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

}
