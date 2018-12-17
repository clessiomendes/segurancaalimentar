
// Place your Spring DSL code here
beans = {
    sessionFactory() {
        dataSource = ref('dataSource')
//        hibernateProperties = ["hibernate.hbm2ddl.auto": "create-drop",
//                               "hibernate.show_sql":     "true"]
    }

}
