package com.campussync.controller.admin;

import com.campussync.dto.ParentStudentLinkResponse;
import com.campussync.dto.request.ParentRequest;
import com.campussync.dto.response.ParentResponse;
import com.campussync.service.ParentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/parents")
@RequiredArgsConstructor
public class AdminParentController {

    private final ParentService parentService;

    // 1. Create Parent
    @PostMapping
    public ResponseEntity<ParentResponse> createParent(
            @Valid @RequestBody ParentRequest request) {

        ParentResponse response = parentService.createParent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get All Parents
    @GetMapping
    public ResponseEntity<List<ParentResponse>> getAllParents() {

        return ResponseEntity.ok(parentService.getAllParents());
    }

    // 3. Get Parent By ID
    @GetMapping("/{parentId}")
    public ResponseEntity<ParentResponse> getParentById(
            @PathVariable Long parentId) {

        return ResponseEntity.ok(
                parentService.getParentById(parentId));
    }

    // 4. Update Parent
    @PutMapping("/{parentId}")
    public ResponseEntity<ParentResponse> updateParent(
            @PathVariable Long parentId,
            @Valid @RequestBody ParentRequest request) {

        return ResponseEntity.ok(
                parentService.updateParent(parentId, request));
    }

    // 5. Link Student to Parent
    @PostMapping("/{parentId}/students/{studentId}")
    public ResponseEntity<ParentStudentLinkResponse> linkStudent(
            @PathVariable Long parentId,
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                parentService.linkStudentToParent(parentId, studentId));
    }

    // 6. Change Student's Parent
    @PutMapping("/students/{studentId}/change/{newParentId}")
    public ResponseEntity<ParentStudentLinkResponse> changeStudentParent(
            @PathVariable Long studentId,
            @PathVariable Long newParentId) {

        return ResponseEntity.ok(
                parentService.changeStudentParent(studentId, newParentId));
    }

    // 7. Unlink Student from Parent
    @DeleteMapping("/students/{studentId}/unlink")
    public ResponseEntity<Void> unlinkStudent(
            @PathVariable Long studentId) {

        parentService.unlinkStudentFromParent(studentId);

        return ResponseEntity.noContent().build();
    }
}
