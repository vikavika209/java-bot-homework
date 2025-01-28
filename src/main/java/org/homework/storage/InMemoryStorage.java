package org.homework.storage;

import lombok.Data;
import org.homework.di.annotations.Register;
import org.homework.model.User;

import java.util.HashSet;
import java.util.Set;

@Data
@Register
public class InMemoryStorage {
    public final Set<User> users;

    public InMemoryStorage() {
        users = new HashSet<>();
    }
}
