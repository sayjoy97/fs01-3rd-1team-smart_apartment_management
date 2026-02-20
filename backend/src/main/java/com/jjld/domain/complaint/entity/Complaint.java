package com.jjld.domain.complaint.entity;

import com.jjld.domain.complaint.dto.user.ComplaintUserUpdate;
import com.jjld.domain.complaint.entity.Enum.ComplaintCategory;
import com.jjld.domain.complaint.entity.Enum.ComplaintStatus;
import com.jjld.domain.house.entity.House;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "complaint")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long complaintId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(nullable = false)
    private String householderEmail;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private ComplaintCategory category;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status = ComplaintStatus.WAITING;

    @CreationTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "complaint",
                cascade = CascadeType.ALL,
                orphanRemoval = true,
                fetch = FetchType.LAZY)
    private ComplaintAnalysis complaintAnalysis;

    @OneToOne(mappedBy = "complaint",
                cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private ComplaintReply complaintReply;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "complaint_reference",
            joinColumns = @JoinColumn(name = "complaint_id"),
            inverseJoinColumns = @JoinColumn(name = "reference_id")
    )
    private Set<Complaint> referenceComplaints = new HashSet<>();

    public void addReferenceComplaint(Complaint ref){
        this.referenceComplaints.add(ref);
    }
}
