package org.example;

public class FieldOrMethodDto {
  private String name;
  private String vari;
  private String scope;
  private String signature;

  public FieldOrMethodDto(String name, String vari, String scope, String signature) {
    this.name = name;
    this.vari = vari;
    this.scope = scope;
    this.signature = signature;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVari() {
    return vari;
  }

  public void setVari(String vari) {
    this.vari = vari;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }
}
