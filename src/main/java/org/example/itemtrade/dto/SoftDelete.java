package org.example.itemtrade.dto;

public interface SoftDelete {
  void softDelete(); // soft delete
  boolean isDeleted(); // check if deleted
}
