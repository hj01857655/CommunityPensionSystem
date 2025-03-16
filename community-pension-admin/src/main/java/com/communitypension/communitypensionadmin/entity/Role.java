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
@TableName("roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 50)
    @NotBlank
    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Size(max = 255)
    @NotBlank
    @Column(name = "role_description")
    private String roleDescription;

    @Lob
    @Column(name = "permissions")
    private String permissions;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;

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