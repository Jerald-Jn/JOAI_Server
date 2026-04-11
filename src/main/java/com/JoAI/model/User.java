package com.JoAI.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String name;
    String password;
    String address;
    String contact_No;

    @Override
    public String toString() {
        return "User [name=" + name + ", password=" + password + ", address=" + address + ", contact_No=" + contact_No
                + "]";
    }
}
