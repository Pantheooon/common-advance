package tdd.arg3;

public enum TypeEnum {


    BOOL {
        @Override
        public String getTypeName() {
            return "bool";
        }

        @Override
        public Object handleValue(String value) {
            return Boolean.valueOf(value);
        }

        @Override
        public Object getDefaultValue() {
            return Boolean.TRUE;
        }
    },
    STR {
        @Override
        public String getTypeName() {
            return "str";
        }

        @Override
        public Object handleValue(String value) {
            return value;
        }

        @Override
        public Object getDefaultValue() {
            return "";
        }
    },
    INT {
        @Override
        public String getTypeName() {
            return "int";
        }

        @Override
        public Object handleValue(String value) {
            return Integer.parseInt(value);
        }

        @Override
        public Object getDefaultValue() {
            return new Integer(0);
        }
    };


    public Object getValue(String value) {
        if (value == null || value.length() == 0) {
            return getDefaultValue();
        }
        return handleValue(value);
    }

    public static TypeEnum getTypeEnum(String flag) {
        TypeEnum[] values = TypeEnum.values();
        for (TypeEnum value : values) {
            if (value.getTypeName().equals(flag)) {
                return value;
            }
        }
        return null;
    }

    public abstract String getTypeName();


    public abstract Object handleValue(String value);

    public abstract Object getDefaultValue();
}
