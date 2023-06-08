package com.example.StoreIT.validator;

import com.example.StoreIT.entity.Category;
import com.example.StoreIT.validator.nnotation.ValidCategoryId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidCategoryIdValidator implements ConstraintValidator<ValidCategoryId, Category> {
    @Override
    public boolean isValid(Category category, ConstraintValidatorContext context ){
        return category != null && category.getId() != null;
    }
}
