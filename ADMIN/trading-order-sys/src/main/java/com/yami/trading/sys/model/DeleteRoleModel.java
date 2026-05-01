package com.yami.trading.sys.model;

import lombok.Data;

@Data
public class DeleteRoleModel {

    private  Long[] roleIds;

    private  String  appType;
}
