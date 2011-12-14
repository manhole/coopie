package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Text;

public class ElementEditors {

    public static ElementEditor passThrough() {
        return PASS_THROUGH;
    }

    public static ElementEditor trim() {
        return TRIM;
    }

    public static ElementEditor trimWhitespace() {
        return TRIM_WHITESPACE;
    }

    /**
     * 何もしません。
     */
    private static final ElementEditor PASS_THROUGH = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element;
        }
    };

    /**
     * {@link String#trim()}します。
     */
    private static final ElementEditor TRIM = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return element.trim();
        }
    };

    /**
     * {@link Character#isWhitespace(char)}である文字をtrimします。
     */
    private static final ElementEditor TRIM_WHITESPACE = new ElementEditor() {
        @Override
        public String edit(final String element) {
            return Text.trimWhitespace(element);
        }
    };

}
