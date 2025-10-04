package com.example.bankcards.entity;

import com.example.bankcards.dto.UserDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@NoArgsConstructor
@Table(name = "users")
@Getter
@Setter
public class CardUser {
    @Id
    private String id;
    private String username;
    private String password;
    private Set<Role> roles;

    public UserDTO toDTO(){
        return new UserDTO(id, username, roles);
    }
}
