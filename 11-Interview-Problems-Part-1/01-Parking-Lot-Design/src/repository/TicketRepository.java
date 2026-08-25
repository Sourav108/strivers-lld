package repository;

import domain.Ticket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TicketRepository {
    private final Map<UUID, Ticket> tickets = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findById(UUID ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public List<Ticket> findActiveTickets() {
        return tickets.values().stream()
                .filter(Ticket::isActive)
                .collect(Collectors.toList());
    }

    public void deactivateTicket(UUID ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null) {
            ticket.deactivate();
        }
    }
}
