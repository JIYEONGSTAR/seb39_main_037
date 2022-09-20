package com.main.project.user;

import com.main.project.badge.UserBadge;
import com.main.project.like.Like;
import com.main.project.location.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

import java.util.ArrayList;
import java.util.List;

import static com.main.project.user.WebUser.authority.REGULAR_USER;

@NoArgsConstructor
@Setter
@Entity
public class WebUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long userid;

    private String userName;

    private String nickName;

    @Email
    private String email;

    private String password;

    private Enum<WebUser.authority> authority = REGULAR_USER;


    @ManyToOne
    @JoinColumn(name = "location_Id")
    private Location location;

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.ALL)
    private List<UserBadge> userBadges = new ArrayList<>();

    @OneToMany(mappedBy = "webUser", cascade = CascadeType.ALL)
    private List<Like> likes = new ArrayList<>();




    public enum authority {

        REGULAR_USER("일반 계정"),
        ADMIN_USER("관리자 게정");


        @Getter
        private final String authority;


        authority(String authority) {
            this.authority =authority;
        }
    }



}
