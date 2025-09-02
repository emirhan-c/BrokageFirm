import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
// ...existing code...
    @GetMapping("/list")
    public List<Order> listOrders(@Valid @RequestBody OrderListRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            // Sadece kendi verisine erişebilir
            // CustomerRepository ile username'den customerId alınabilir
            // (Burada örnek olarak username = customerId varsayılmıştır, gerekirse repository ile bulunabilir)
            if (!request.getCustomerId().toString().equals(username)) {
                throw new org.springframework.security.access.AccessDeniedException("You can only access your own orders.");
            }
        }
        return orderService.list(request.getCustomerId(), request.getStartDate(), request.getEndDate());
    }
// ...existing code...

