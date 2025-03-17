package com.example.guiex1.domain;

import com.example.guiex1.domain.Prietenie;
import com.example.guiex1.domain.ValidationException;
import com.example.guiex1.domain.Validator;

public class PrietenieValidator implements Validator<Prietenie> {
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        //TODO: implement method validate
        if(entity.getId().getLeft().equals("") || entity.getId().getRight().equals("")) {
            throw new ValidationException("Prietenie nu este valid");
        }
    }
}
