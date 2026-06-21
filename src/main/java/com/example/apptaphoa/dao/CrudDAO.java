/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.apptaphoa.dao;

import java.util.List;

/**
 *
 * @author Boo6
 */
public interface CrudDAO<Entity, String> {
    Entity create(Entity entity);
    void update(Entity entity);
    void deleteById(String id);
    List<Entity> findAll();
    Entity findById(String id);
    Entity findById(int id);

}
