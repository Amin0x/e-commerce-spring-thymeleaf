package com.alamin.ecommerce.settings;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_settings")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String domainName;
    private String apiKeyValue;
    private Integer maxProductCount;
    private Boolean bannerEnable;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getApiKeyValue() {
        return apiKeyValue;
    }

    public void setApiKeyValue(String apiKeyValue) {
        this.apiKeyValue = apiKeyValue;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Integer getMaxProductCount() {
        return maxProductCount;
    }

    public void setMaxProductCount(Integer maxProductCount) {
        this.maxProductCount = maxProductCount;
    }

    public Boolean getBannerEnable() {
        return bannerEnable;
    }

    public void setBannerEnable(Boolean bannerEnable) {
        this.bannerEnable = bannerEnable;
    }
}
