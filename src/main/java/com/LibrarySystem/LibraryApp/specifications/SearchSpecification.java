package com.LibrarySystem.LibraryApp.specifications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotEmpty;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public class SearchSpecification<E extends Serializable> implements Specification<E> {

    private String key;
    private String[] value;
    private boolean precision = false;

    public SearchSpecification(String key, String value) {
        this.key = key;
        this.value = new String[] { value };
    }

    public SearchSpecification(String key, String[] value) {
        this.key = key;
        this.value = value;
    }

    public SearchSpecification(String key, Integer value) {
        this.key = key;
        this.value = new String[] { value.toString() };
        this.precision = true;
    }

    public SearchSpecification(String key, Integer[] value) {
        this.key = key;
        List<String> valList = new ArrayList<>();
        for (Integer integer : value) {
            valList.add(integer.toString());
        }
        this.value = valList.toArray(String[]::new);
        this.precision = true;
    }

    public SearchSpecification<E> setPrecision(boolean precision) {
        this.precision = precision;
        return this;
    }

    public static <N extends Number, T extends Serializable> Specification<T> parseSearchSpecificationListNumber(
            String key, @NotEmpty List<N> val) {

        Specification<T> specificationInner = null;

        for (N valIter : val) {
            Integer valInteger = valIter.intValue();

            if (specificationInner == null) {
                specificationInner = Specification.where(new SearchSpecification<T>(key, valInteger));
                continue;
            }

            specificationInner = specificationInner.or(new SearchSpecification<T>(key, valInteger));
        }

        return specificationInner;
    }

    public static <T extends Serializable> Specification<T> parseSearchSpecificationListString(String key,
            @NotEmpty List<String> val) {

        Specification<T> specificationInner = null;

        for (String valIter : val) {

            if (specificationInner == null) {
                specificationInner = Specification.where(new SearchSpecification<T>(key, valIter));
                continue;

            }

            specificationInner = specificationInner.or(new SearchSpecification<T>(key, valIter));
        }

        return specificationInner;
    }

    public static <T> Boolean isValidToParseSearchSpecificationList(List<T> val) {

        if (val == null)
            return false;

        if (val.isEmpty())
            return false;

        return true;
    }

    public Specification<E> getSpecification() {

        Specification<E> specificationInner = null;
        
        for (String valIter : value) {

            if (specificationInner == null) {
                specificationInner = Specification.where(new SearchSpecification<E>(key, valIter).setPrecision(precision));
                continue;

            }

            specificationInner = specificationInner.or(new SearchSpecification<E>(key, valIter).setPrecision(precision));
        }

        return specificationInner;

        //return SearchSpecification.parseSearchSpecificationListString(key, Arrays.asList(value));
    }

    @Override
    @Nullable
    public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate predicate = null;

        if (precision) {
            // predicate = criteriaBuilder.like(root.get(key).as(String.class), "%" + value
            // + "%");
            for (String str : value) {
                if (predicate == null) {
                    predicate = criteriaBuilder.equal(root.get(key).as(String.class), str);
                    continue;
                }

                predicate = criteriaBuilder.or(predicate, criteriaBuilder.equal(root.get(key).as(String.class), str));

            }

        }
        else {
            // predicate = criteriaBuilder.like(root.get(key).as(String.class), "%" + value
            // + "%");
            for (String str : value) {
                if (predicate == null) {
                    predicate = criteriaBuilder.like(root.get(key).as(String.class), "%" + str + "%");
                    continue;
                }

                predicate = criteriaBuilder.or(predicate,
                        criteriaBuilder.like(root.get(key).as(String.class), "%" + str + "%"));
            }

        }

        return predicate;
    }

    public String getKey() {
        return key;
    }

    public String[] getValue() {
        return value;
    }

    public boolean isPrecise() {
        return precision;
    }

    @Override
    public String toString() {
        return "SearchSpecification [key=" + key + ", value=" + String.join(",", value).toString() + "]";
    }

}
