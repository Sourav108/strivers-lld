package service;

import domain.Ticket;
import domain.Vehicle;
import repository.TicketRepository;

import java.util.Optional;
import java.util.UUID;

public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket generateTicket(Vehicle vehicle, UUID slotId) {
        Ticket ticket = new Ticket(vehicle.getId(), slotId);
        return ticketRepository.save(ticket);
    }

    public Optional<Ticket> getTicket(UUID ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public void deactivateTicket(UUID ticketId) {
        ticketRepository.deactivateTicket(ticketId);
    }
}
