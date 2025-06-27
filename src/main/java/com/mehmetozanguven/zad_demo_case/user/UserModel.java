package com.mehmetozanguven.zad_demo_case.user;

import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class UserModel extends ApiBaseModel {
    private String email;
}
