package jp.sourceforge.hotchpotch.coopie.csv;

public interface ConverterRepository {

    Converter detect(CsvColumnDef columnDef);

}
