package com.bookinggo.hackaton.domain.script;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.GenerationType.SEQUENCE;
import static javax.persistence.TemporalType.TIMESTAMP;
import static lombok.AccessLevel.PROTECTED;

@Table
@Entity
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
class ScriptEntity {

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = SEQUENCE)
    private Long id;

    @Temporal(TIMESTAMP)
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @Temporal(TIMESTAMP)
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastUpdatedDate;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String location;

    @ManyToOne
    private ScriptOwnerEntity owner;

}
