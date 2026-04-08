//package com.JVM.eCart.category.entity;
//
//import jakarta.persistence.*;
//
//@Entity
//@Table(name = "category_metadata_field_mapping")
//public class CategoryMetaDataFieldMapping {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "category_id")
//    private Category category;
//
//    @ManyToOne
//    @JoinColumn(name = "category_metadata_field_id")
//    private CategoryMetaDataField field;
//}
