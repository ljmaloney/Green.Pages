package com.green.yp.api.apitype.account;

public enum AccountRoleType {
  ADMIN("GreenPages-Admin"),
  SUBSCRIBER_ADMIN("Greepages-SubscriberAdmin"),
  SUBSCRIBER("Greepages-Subscriber"),
  SYS_ADMIN("SysAdmin");
  private final String role;

  AccountRoleType(String roleName) {
    this.role = roleName;
  }

  public String getRole() {
    return role;
  }
}
