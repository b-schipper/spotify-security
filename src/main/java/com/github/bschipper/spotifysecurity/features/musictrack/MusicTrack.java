package com.github.bschipper.spotifysecurity.features.musictrack;

import com.github.bschipper.spotifysecurity.features.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity(name = "musictracks")
public class MusicTrack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private float duration;

    @ManyToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User artist;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "musictrack_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "musictrack_id"))
    private Set<User> userLikes;
}
