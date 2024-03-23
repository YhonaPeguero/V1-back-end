package org.example;

import java.util.List;

public class ResponseDto {
  private String className;
  private String scope;
  private String constructor;
  private List<FieldOrMethodDto> data;

  public ResponseDto(String className, String scope, String constructor, List<FieldOrMethodDto> data) {
    this.className = className;
    this.scope = scope;
    this.constructor = constructor;
    this.data = data;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getConstructor() {
    return constructor;
  }

  public void setConstructor(String constructor) {
    this.constructor = constructor;
  }

  public List<FieldOrMethodDto> getData() {
    return data;
  }

  public void setData(List<FieldOrMethodDto> data) {
    this.data = data;
  }
}
