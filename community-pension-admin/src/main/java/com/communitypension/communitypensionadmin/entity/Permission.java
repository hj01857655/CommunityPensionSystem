package com.communitypension.communitypensionadmin.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Entity
@Data
@TableName("permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotBlank
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Size(max = 50)
    @NotBlank
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;

    @Size(max = 50)
    @NotBlank
    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "sort")
    private Integer sort = 0;

    @Size(max = 200)
    @Column(name = "description", length = 200)
    private String description;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = true;

    @Column(name = "created_by")
    private Long createdBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;
}