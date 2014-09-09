package com.zero.votes.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Poll implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String title;
    private String description;
    private final String previewToken;
    private PollState pollState;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private boolean participationTracking;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderBy("username ASC")
    private Set<Organizer> organizers;

    @OneToMany(mappedBy = "poll", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @OrderBy("title ASC")
    private Set<Item> items;

    @OneToMany(mappedBy = "poll", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @OrderBy("email ASC")
    private Set<Participant> participants;

    @OneToMany(mappedBy = "poll", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @OrderBy("id ASC")
    private Set<Token> tokens;

    public Poll() {
        this.pollState = PollState.PREPARING;
        this.organizers = new HashSet<Organizer>();
        this.items = new HashSet<Item>();
        this.participants = new HashSet<Participant>();
        this.tokens = new HashSet<Token>();
        this.previewToken = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }
    
    public PollState getPollState() {
         return this.pollState;
    }

    public void setPollState(PollState pollState) {
        this.pollState = pollState;
    }

    public boolean isPollFinished() {
        return this.pollState.equals(PollState.FINISHED);
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        if (!this.isPollFinished()) {
            this.endDate = endDate;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean isParticipationTracking() {
        return participationTracking;
    }

    public void setParticipationTracking(boolean participationTracking) {
        this.participationTracking = participationTracking;
    }

    public Set<Organizer> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(Set<Organizer> organizers) {
        this.organizers = organizers;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public Set<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<Participant> participants) {
        this.participants = participants;
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public void setTokens(Set<Token> tokens) {
        this.tokens = tokens;
    }

    public String getPreviewToken() {
        return previewToken;
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Poll other = (Poll) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
    

}
