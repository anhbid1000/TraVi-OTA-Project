package com.ota.travi.controller;

import com.ota.travi.dto.request.BanRequest;
import com.ota.travi.dto.request.ComboRequest;
import com.ota.travi.dto.request.MenuItemStatusRequest;
import com.ota.travi.dto.request.MonAnRequest;
import com.ota.travi.dto.request.PartnerBusinessProfileRequest;
import com.ota.travi.dto.request.PhongUpsertRequest;
import com.ota.travi.dto.request.ThucDonRequest;
import com.ota.travi.dto.response.FileUploadResponse;
import com.ota.travi.dto.response.BanResponse;
import com.ota.travi.dto.response.ComboResponse;
import com.ota.travi.dto.response.HoSoKinhDoanhResponse;
import com.ota.travi.dto.response.MonAnResponse;
import com.ota.travi.dto.response.PhongResponse;
import com.ota.travi.dto.response.ThucDonResponse;
import com.ota.travi.security.CustomUserDetails;
import com.ota.travi.service.FileStorageService;
import com.ota.travi.service.PartnerBusinessProfileService;
import com.ota.travi.service.PartnerHotelService;
import com.ota.travi.service.PartnerRestaurantService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import static com.ota.travi.constant.ApiEndpoints.PARTNER_BUSINESS_PROFILES;
import static com.ota.travi.constant.ApiEndpoints.PARTNER_HOTELS;
import static com.ota.travi.constant.ApiEndpoints.PARTNER_LEGACY_BUSINESS_PROFILES;
import static com.ota.travi.constant.ApiEndpoints.PARTNER_RESTAURANTS;

@RestController
@PreAuthorize("hasRole('DOI_TAC')")
public class PartnerAssetController {
    private final PartnerBusinessProfileService partnerBusinessProfileService;
    private final PartnerHotelService partnerHotelService;
    private final PartnerRestaurantService partnerRestaurantService;
    private final FileStorageService fileStorageService;

    public PartnerAssetController(
            PartnerBusinessProfileService partnerBusinessProfileService,
            PartnerHotelService partnerHotelService,
            PartnerRestaurantService partnerRestaurantService,
            FileStorageService fileStorageService
    ) {
        this.partnerBusinessProfileService = partnerBusinessProfileService;
        this.partnerHotelService = partnerHotelService;
        this.partnerRestaurantService = partnerRestaurantService;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping(PARTNER_BUSINESS_PROFILES)
    public ResponseEntity<?> getBusinessProfiles(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return new ResponseEntity<>(
                partnerBusinessProfileService.getBusinessProfiles(userDetails.getUser().getId()),
                HttpStatus.OK
        );
    }

    @GetMapping(PARTNER_BUSINESS_PROFILES + "/{id}")
    public ResponseEntity<?> getBusinessProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        HoSoKinhDoanhResponse response = partnerBusinessProfileService.getBusinessProfile(userDetails.getUser().getId(), id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(PARTNER_BUSINESS_PROFILES + "/uploads")
    public ResponseEntity<?> uploadPartnerAsset(@RequestParam("file") MultipartFile file) {
        FileUploadResponse response = fileStorageService.storePartnerAsset(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping({PARTNER_LEGACY_BUSINESS_PROFILES, PARTNER_BUSINESS_PROFILES})
    public ResponseEntity<?> createBusinessProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PartnerBusinessProfileRequest request
    ) {
        HoSoKinhDoanhResponse response = partnerBusinessProfileService.createBusinessProfile(userDetails.getUser().getId(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(PARTNER_BUSINESS_PROFILES + "/{id}")
    public ResponseEntity<?> updateBusinessProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody PartnerBusinessProfileRequest request
    ) {
        HoSoKinhDoanhResponse response = partnerBusinessProfileService.updateBusinessProfile(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(PARTNER_HOTELS + "/{id}/rooms")
    public ResponseEntity<?> createRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody PhongUpsertRequest request
    ) {
        PhongResponse response = partnerHotelService.createRoom(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(PARTNER_HOTELS + "/{id}/rooms")
    public ResponseEntity<?> getRooms(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(partnerHotelService.getRooms(userDetails.getUser().getId(), id), HttpStatus.OK);
    }

    @PutMapping(PARTNER_HOTELS + "/{id}/rooms/{roomId}")
    public ResponseEntity<?> updateRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String roomId,
            @Valid @RequestBody PhongUpsertRequest request
    ) {
        PhongResponse response = partnerHotelService.updateRoom(userDetails.getUser().getId(), id, roomId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(PARTNER_HOTELS + "/{id}/rooms/{roomId}")
    public ResponseEntity<?> deleteRoom(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String roomId
    ) {
        partnerHotelService.deleteRoom(userDetails.getUser().getId(), id, roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(PARTNER_RESTAURANTS + "/{id}/tables")
    public ResponseEntity<?> createTable(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody BanRequest request
    ) {
        BanResponse response = partnerRestaurantService.createTable(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(PARTNER_RESTAURANTS + "/{id}/tables")
    public ResponseEntity<?> getTables(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(partnerRestaurantService.getTables(userDetails.getUser().getId(), id), HttpStatus.OK);
    }

    @PutMapping(PARTNER_RESTAURANTS + "/{id}/tables/{tableId}")
    public ResponseEntity<?> updateTable(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String tableId,
            @Valid @RequestBody BanRequest request
    ) {
        BanResponse response = partnerRestaurantService.updateTable(userDetails.getUser().getId(), id, tableId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(PARTNER_RESTAURANTS + "/{id}/tables/{tableId}")
    public ResponseEntity<?> deleteTable(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String tableId
    ) {
        partnerRestaurantService.deleteTable(userDetails.getUser().getId(), id, tableId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(PARTNER_RESTAURANTS + "/{id}/menus")
    public ResponseEntity<?> createMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody ThucDonRequest request
    ) {
        ThucDonResponse response = partnerRestaurantService.createMenu(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(PARTNER_RESTAURANTS + "/{id}/menus")
    public ResponseEntity<?> getMenus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(partnerRestaurantService.getMenus(userDetails.getUser().getId(), id), HttpStatus.OK);
    }

    @PutMapping(PARTNER_RESTAURANTS + "/{id}/menus/{menuId}")
    public ResponseEntity<?> updateMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String menuId,
            @Valid @RequestBody ThucDonRequest request
    ) {
        ThucDonResponse response = partnerRestaurantService.updateMenu(userDetails.getUser().getId(), id, menuId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(PARTNER_RESTAURANTS + "/{id}/menus/{menuId}")
    public ResponseEntity<?> deleteMenu(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String menuId
    ) {
        partnerRestaurantService.deleteMenu(userDetails.getUser().getId(), id, menuId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(PARTNER_RESTAURANTS + "/{id}/menu-items")
    public ResponseEntity<?> createMenuItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody MonAnRequest request
    ) {
        MonAnResponse response = partnerRestaurantService.createMenuItem(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(PARTNER_RESTAURANTS + "/{id}/menu-items")
    public ResponseEntity<?> getMenuItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(partnerRestaurantService.getMenuItems(userDetails.getUser().getId(), id), HttpStatus.OK);
    }

    @PutMapping(PARTNER_RESTAURANTS + "/{id}/menu-items/{itemId}")
    public ResponseEntity<?> updateMenuItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String itemId,
            @Valid @RequestBody MonAnRequest request
    ) {
        MonAnResponse response = partnerRestaurantService.updateMenuItem(userDetails.getUser().getId(), id, itemId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(PARTNER_RESTAURANTS + "/{id}/menu-items/{itemId}")
    public ResponseEntity<?> deleteMenuItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String itemId
    ) {
        partnerRestaurantService.deleteMenuItem(userDetails.getUser().getId(), id, itemId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(PARTNER_RESTAURANTS + "/{id}/menu-items/{itemId}/status")
    public ResponseEntity<?> updateMenuItemStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String itemId,
            @Valid @RequestBody MenuItemStatusRequest request
    ) {
        MonAnResponse response = partnerRestaurantService.updateMenuItemStatus(
                userDetails.getUser().getId(),
                id,
                itemId,
                request.trangThai()
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(PARTNER_RESTAURANTS + "/{id}/combos")
    public ResponseEntity<?> createCombo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @Valid @RequestBody ComboRequest request
    ) {
        ComboResponse response = partnerRestaurantService.createCombo(userDetails.getUser().getId(), id, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(PARTNER_RESTAURANTS + "/{id}/combos")
    public ResponseEntity<?> getCombos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id
    ) {
        return new ResponseEntity<>(partnerRestaurantService.getCombos(userDetails.getUser().getId(), id), HttpStatus.OK);
    }

    @PutMapping(PARTNER_RESTAURANTS + "/{id}/combos/{comboId}")
    public ResponseEntity<?> updateCombo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String comboId,
            @Valid @RequestBody ComboRequest request
    ) {
        ComboResponse response = partnerRestaurantService.updateCombo(userDetails.getUser().getId(), id, comboId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(PARTNER_RESTAURANTS + "/{id}/combos/{comboId}")
    public ResponseEntity<?> deleteCombo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable String id,
            @PathVariable String comboId
    ) {
        partnerRestaurantService.deleteCombo(userDetails.getUser().getId(), id, comboId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

