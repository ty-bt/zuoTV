package com.mtv

class CollectGroup {

    /**
     * 收藏列表类型
     */
    static enum COLLECT_TYPE {
        Collect("收藏列表", "collect"),
        ONCE("同时观看", "average"),

        public String description
        public String value

        public COLLECT_TYPE(String description, String value) {
            this.description = description
            this.value = value
        }

        public String toString() {
            return this.description
        }
    }

    User user

    String name

    String type

    static constraints = {
    }
}
