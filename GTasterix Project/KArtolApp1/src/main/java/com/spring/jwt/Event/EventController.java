package com.spring.jwt.Event;

import com.spring.jwt.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "Event", description = "APIs for managing events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Operation(summary = "Create a new Event")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EventDto>> createEvent(
            @Parameter(description = "Event details required",required = true)
            @Valid @RequestBody EventDto eventDto){
        try {
            EventDto result = eventService.createEvent(eventDto);
            return ResponseEntity.ok(ApiResponse.success("Event created successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to create Event", e.getMessage()));
        }
    }

    @Operation(summary = "Get an event by ID", description = "Retrieves an event by its unique identifier")
    @GetMapping("/byId/{id}")
    public ResponseEntity<ApiResponse<EventDto>> eventById(
            @Parameter(description = "Event id",required = true,example = "1")
            @PathVariable @Min(1) Integer id){
        try {
            EventDto dto = eventService.getEventById(id);
            return ResponseEntity.ok(ApiResponse.success("Event fetched successfully", dto));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Event not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch event", e.getMessage()));
        }
    }

    @Operation(summary = "get event at particular date", description = "Retrieves events at particular date")
    @GetMapping("/byDate")
    public ResponseEntity<ApiResponse<List<EventDto>>> eventsByDate(
            @Parameter(description = "Events at particular Date", required = true, example = "10-06-2025")
            @RequestParam("date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date){
        try {
            List<EventDto> allEventsByDate = eventService.getAllEventsByDate(date);
            return ResponseEntity.ok(ApiResponse.success("All events fetched successfully", allEventsByDate));
        }catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "No Events Found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to Fetch Events",e.getMessage()));
        }
    }


    @Operation(summary = "Get all Events with pagination", description = "Retrieves all Events with pagination and sorting options")
    @GetMapping("/allEvents")
    public ResponseEntity<ApiResponse<Page<EventDto>>> getAllEvents(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(defaultValue = "eventId") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)", example = "asc")
            @RequestParam(defaultValue = "asc") String direction
    ) {
        try {
            Sort sort = direction.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<EventDto> result = eventService.getAllEvents(pageable);

            return ResponseEntity.ok(ApiResponse.success("All Events fetched successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to fetch Events", e.getMessage()));
        }

    }

    @Operation(summary = "Delete an event", description = "Deletes an event by ID")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>>deleteEvent(
            @Parameter(description = "Event Id",required = true,example = "1")
            @RequestParam @Min(1) Integer id){
        try{
            eventService.deleteEvent(id);
            return ResponseEntity.ok(ApiResponse.success("Event Deleted Successfully"));
        }catch(EventNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND,"Event not found",e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST,"Failed to delete event",e.getMessage()));
        }

    }

    @Operation(summary = "Update an event", description = "Updates an existing event by ID")
    @PatchMapping("/update/{id}")
    public ResponseEntity<ApiResponse<EventDto>> updateEvent(
            @Parameter(description = "Event ID", required = true, example = "1")
            @PathVariable @Min(1) Integer id,
            @Parameter(description = "Event details to update", required = true)
            @Valid @RequestBody EventDto eventDto
    ) {
        try {
            EventDto updated = eventService.updateEvent(id, eventDto);
            return ResponseEntity.ok(ApiResponse.success("Event updated successfully", updated));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Event not found", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, "Failed to update Event", e.getMessage()));
        }
    }



}
