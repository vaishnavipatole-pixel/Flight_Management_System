package AirlineManagementSystem;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
public class AirlineManagementSystem {
	enum UserRole {PASSENGER, CREW_ADMIN 
	}
	    private static final Scanner SC = new Scanner(System.in);
	  //Formatter for date and time(day-month-year hour:minute)
	    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
	    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
	    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
	    private static final Pattern PASSPORT_PATTERN = Pattern.compile("^[A-Za-z0-9]{6,12}$");
	    //Map stores key-value pairs,LinkedHashMap important for display
	    private final Map<String, Flight> flights = new LinkedHashMap<String, Flight>();
	    private final Map<String, Booking> bookingsByPnr = new LinkedHashMap<String, Booking>();
	    private final Map<String, List<Booking>> bookingsByEmail = new HashMap<String, List<Booking>>();
	    private final Map<String, List<Booking>> groupBookings = new HashMap<String, List<Booking>>();

	    private final List<CrewMember> crewMembers = new ArrayList<CrewMember>();
	    private final List<Feedback> feedbacks = new ArrayList<Feedback>();

	    private final AlertCenter alertCenter = new AlertCenter();

	    public static void main(String[] args) {
	        AirlineManagementSystem app = new AirlineManagementSystem();
	        app.seedData();
	        app.run();
	    }

	    private boolean handlePassengerChoice(int choice) {
	        switch (choice) {
	            case 1: viewFlightsAndSeatPattern(); break;
	            case 2: bookSinglePassenger(); break;
	            case 3: bookGroupPassengers(); break;
	            case 4: cancelBooking(); break;
	            case 5: viewPassengerBookings(); break;
	            case 6: showMealMenu(); break;
	            case 7: entertainmentSection(); break;
	            case 8: alertCenter.showSafetyAlerts(); break;
	            case 9: passengerCheckInAndUpgrade(); break;
	            case 10: printTicketAndPassport(); break;
	            case 11: submitFeedback(); break;
	            case 12: 
	                System.out.println("Thank you for using SafeCircle: AlertShield.");
	                return false;
	            default: System.out.println("Invalid option.");
	        }
	        return true;
	    }

	 // FIX: make sure this method exists
	    private boolean handleCrewAdminChoice(int choice) {
	        switch (choice) {
	            case 1: crewAndAirHostessManagement(); break;
	            case 2: operationsDashboard(); break;
	            case 3: showFlightMonitoringStatus(); break;
	            case 4: alertCenter.showEmergencyAlerts(); break;
	            case 5: showPassengerList(); break;
	            case 6: 
	                System.out.println("Exiting Crew/Admin menu.");
	                System.out.println("SIGNING OFF FROM SAFECIRCLE ALERTSHIELD!!!");
	                return false;
	            default: 
	                System.out.println("Invalid option.");
	        }
	        return true;
	    }
	    private void showPassengerList() {
	        System.out.println("\n========== PASSENGER LIST ==========");
	        if (bookingsByPnr.isEmpty()) {
	            System.out.println("No passengers booked yet.");
	            return;
	        }

	        String flightNo = readLine("Enter flight number to filter (or press Enter to show all): ").toUpperCase();

	        int count = 0;        //Initialization-to count no.of passenger
	        for (Booking b : bookingsByPnr.values()) {
	            if (flightNo.isEmpty() || b.flight.flightNo.equalsIgnoreCase(flightNo)) {
	                printBookingShort(b);
	                System.out.println("--------------------------------------------");
	                count++;
	            }
	        }

	        if (count == 0) {
	            System.out.println("No passengers found for flight " + flightNo);
	        } else {
	            System.out.println("Total passengers shown: " + count);
	        }
	    }

	    private void showFlightMonitoringStatus() {
	        System.out.println("\n========== FLIGHT MONITORING ==========");
	        printFlightsTable(); // already prints Flight No, Origin/Dest, Departure/Arrival, Seats, Gate, Status, Weather
	    }



	    private void printBanner() {
	        System.out.println("====================================================================");
	        System.out.println("                     SAFECIRCLE : ALERTSHIELD");
	        System.out.println("            Secure & Safe Flight Management System");
	        System.out.println("====================================================================");
	        System.out.println("System Time: " + LocalDateTime.now().format(DT));
	    }
	 // 👇 ADD THIS RIGHT AFTER printBanner()
	    private UserRole chooseRole() {
	        System.out.println("Select Role: 1.Passenger  2.Crew/Admin");
	        int r = readIntInRange("Choice: ", 1, 2);
	        return (r == 1) ? UserRole.PASSENGER : UserRole.CREW_ADMIN;
	    }
	    // That method control entire program flow
	    private void run() {
	        printBanner();
	        boolean running = true;
	        while (running) {
	            UserRole role = chooseRole();  // always ask for role

	            boolean inRole = true;
	            while (inRole) {
	                if (role == UserRole.PASSENGER) {
	                    printPassengerMenu();
	                    int choice = readInt("Enter your choice: ");
	                    inRole = handlePassengerChoice(choice); // false when passenger exits
	                } else {
	                    printCrewAdminMenu();
	                    int choice = readInt("Enter your choice: ");
	                    inRole = handleCrewAdminChoice(choice); // false when crew exits
	                }
	                System.out.println();
	            }
	            // when inRole becomes false, loop goes back and asks for role again
	        }
	    }

	    private void printPassengerMenu() {
	        System.out.println("\n--------------------- PASSENGER MENU ---------------------");
	        System.out.println("1. View Flights + Seat Pattern");
	        System.out.println("2. Book Flight");
	        System.out.println("3. Group Booking (Discount)");
	        System.out.println("4. Cancel Booking");
	        System.out.println("5. View My Bookings");
	        System.out.println("6. Meal Menu");
	        System.out.println("7. Entertainment (Games/Movies)");
	        System.out.println("8. Passenger Safety Alerts");
	        System.out.println("9. Check-in");
	        System.out.println("10. Print Ticket / Boarding Pass");
	        System.out.println("11. Give Feedback");
	        System.out.println("12. Exit");
	        System.out.println("----------------------------------------------------------");
	    }

	    private void printCrewAdminMenu() {
	        System.out.println("\n--------------------- CREW/ADMIN MENU ---------------------");
	        System.out.println("1. Crew Management");
	        System.out.println("2. Operations Dashboard");
	        System.out.println("3. Flight Status Monitoring");
	        System.out.println("4. Crew Emergency Alerts");
	        System.out.println("5. Passenger List");
	        System.out.println("6. Exit");
	        System.out.println("-----------------------------------------------------------");
	    }

	    // ========================= FLIGHTS =========================

	    private void viewFlightsAndSeatPattern() {
	        printFlightsTable();
	        String flightNo = readLine("Enter flight number to view seat map (or Enter to skip): ").toUpperCase();
	        if (flightNo.length() == 0) return;

	        Flight f = flights.get(flightNo);
	        if (f == null) {
	            System.out.println("❌Flight not found.");
	            return;
	        }

	        System.out.println("\nLegend: O=Available, X=Booked, D*=Disabled Reserved");
	        System.out.println("Economy Seat Map:");
	        f.printSeatPattern(CabinClass.ECONOMY, 6);
	        System.out.println("Business Seat Map:");
	        f.printSeatPattern(CabinClass.BUSINESS, 4);
	    }

	    private void printFlightsTable() {
	        System.out.println("\n======================= FLIGHTS =======================");
	        System.out.printf("%-8s %-12s %-12s %-17s %-17s %-6s %-6s %-6s %-12s %-10s%n",
	                "Flight", "Origin", "Dest", "Departure", "Arrival", "Eco", "Bus", "Gate", "Status", "Weather");
	        System.out.println("---------------------------------------------------------------------------------------------------------------");
	        for (Flight f : flights.values()) {
	            System.out.printf("%-8s %-12s %-12s %-17s %-17s %-6d %-6d %-6s %-12s %-10s%n",
	                    f.flightNo, f.origin, f.destination, f.departure.format(DT), f.arrival.format(DT),
	                    f.getAvailableSeats(CabinClass.ECONOMY), f.getAvailableSeats(CabinClass.BUSINESS),
	                    f.gateNo, f.status, f.weatherNote);
	        }
	        System.out.println("================================================================");
	    }

	    // ========================= BOOKING =========================

	    private void bookSinglePassenger() {
	        Booking booking = bookingFlow(null, 0.0);
	        if (booking != null) {
	            saveBooking(booking, null);// Single passenger booking
	            System.out.println("\n************** BOOKING CONFIRMED **************");
	            printBookingShort(booking);
	            System.out.println("***********************************************");
	        }
	    }

	    private void bookGroupPassengers() {
	        printFlightsTable();
	        String flightNo = readLine("Enter flight number for group booking: ").toUpperCase();
	        Flight flight = flights.get(flightNo);
	        if (flight == null) {
	            System.out.println("Invalid flight!!");
	            return;
	        }

	        int count = readIntInRange("Enter number of passengers (2-20): ", 2, 20);
	        double groupDiscountPercent = (count >= 7) ? 15.0 : (count >= 4 ? 10.0 : 5.0); // ternary operator logic
	        String groupId = "GRP-" + (100 + groupBookings.size() + 1);

	        List<Booking> groupList = new ArrayList<Booking>();
	        System.out.println("Group ID: " + groupId + " | Group Discount: " + groupDiscountPercent + "%");

	        for (int i = 1; i <= count; i++) {
	            System.out.println("\n--- Passenger " + i + " ---");
	            Booking b = bookingFlow(flight, groupDiscountPercent);
	            if (b == null) {
	                rollbackGroup(groupList);
	                System.out.println("Group booking cancelled due to input/payment issue.");
	                return;
	            }
	            saveBooking(b, groupId);
	            groupList.add(b);
	        }

	        groupBookings.put(groupId, groupList);

	        double total = 0.0;
	        for (Booking b : groupList) total += b.finalFare;

	        System.out.println("\n========== GROUP BOOKING SUCCESS ==========");
	        System.out.println("Group ID: " + groupId);
	        System.out.println("Passengers Booked: " + groupList.size());
	        System.out.printf("Grand Total Paid: INR %.2f%n", total);
	        System.out.println("===========================================");
	    }

	    private void rollbackGroup(List<Booking> groupList) {
	        for (Booking b : groupList) {
	            b.flight.cancelSeat(b.cabinClass, b.seatNo);// Using PNR cancel booking
	            bookingsByPnr.remove(b.pnr);
	            String key = b.passenger.gmail.toLowerCase();
	            List<Booking> list = bookingsByEmail.get(key);
	            if (list != null) list.remove(b);
	        }
	    }

	    private Booking bookingFlow(Flight fixedFlight, double groupDiscountPercent) {
	        Flight flight = fixedFlight;// Assume flight is already given
	        if (flight == null) {
	            printFlightsTable();
	            String flightNo = readLine("Enter flight number to book: ").toUpperCase();
	            flight = flights.get(flightNo);
	            if (flight == null) {
	                System.out.println("❌Flight not found.");
	                return null;
	            }
	        }

	        Passenger passenger = readPassengerDetails();
	        if (passenger == null) return null;

	        CabinClass cabinClass = chooseCabinClass();
	        // seats are not available 
	        if (flight.getAvailableSeats(cabinClass) <= 0) {
	            System.out.println("Selected class is full.");
	            return null;
	        }

	        System.out.println("\nSeat Pattern (" + cabinClass + "):");
	        flight.printSeatPattern(cabinClass, cabinClass == CabinClass.ECONOMY ? 6 : 4);

	        List<String> disabledSeats = flight.getAvailableDisabledSeats(cabinClass);
	        if (!disabledSeats.isEmpty()) {
	            System.out.println("Disabled Reserved seats in " + cabinClass + ": " + disabledSeats);
	        }

	        String seatNo = readValidSeatNumber("Enter seat number (E01/B02): ", cabinClass);

	        if (!flight.isSeatAvailable(cabinClass, seatNo)) {
	            System.out.println("Seat not available.");
	            return null;
	        }

	        if (!passenger.disabledPassenger && flight.isDisabledSeat(seatNo, cabinClass)
	                && flight.hasNormalSeatAvailable(cabinClass)) {
	            System.out.println("Selected seat is reserved for disabled passengers.");
	            return null;
	        }

	        String meal = chooseMealByClass(cabinClass);
	        double baggageWeight = readDoubleNonNegative("Enter baggage weight in kg: ");

	        double baseFare = calculateDynamicBaseFare(flight, cabinClass);
            // Age discount calculation
	        double ageDiscountPercent = getAgeDiscountPercent(passenger.age);
	        double ageDiscountAmount = baseFare * ageDiscountPercent / 100.0;
             // Disable discount calculation
	        double disabledDiscountPercent = passenger.disabledPassenger ? 10.0 : 0.0;
	        double afterAge = baseFare - ageDiscountAmount;
	        double disabledDiscountAmount = afterAge * disabledDiscountPercent / 100.0;

	        double afterDisabled = afterAge - disabledDiscountAmount;
	        double groupDiscountAmount = afterDisabled * groupDiscountPercent / 100.0;

	        double allowance = (cabinClass == CabinClass.BUSINESS) ? 30.0 : 20.0;
	        double excess = Math.max(0.0, baggageWeight - allowance);
	        double baggageFee = excess * 350.0;

	        double subtotal = afterDisabled - groupDiscountAmount + baggageFee;
	        double tax = subtotal * 0.05;
	        double finalFare = subtotal + tax;

	        System.out.println("\n---------------- Fare Breakdown ----------------");
	        System.out.printf("Base Fare (Dynamic)                   : INR %.2f%n", baseFare);
	        System.out.printf("Age Discount (%.1f%%)                  : -INR %.2f%n", ageDiscountPercent, ageDiscountAmount);
	        System.out.printf("Disabled Discount (%.1f%%)             : -INR %.2f%n", disabledDiscountPercent, disabledDiscountAmount);
	        System.out.printf("Group Discount (%.1f%%)                : -INR %.2f%n", groupDiscountPercent, groupDiscountAmount);
	        System.out.printf("Baggage Fee (Allow %.1fkg, Extra %.1fkg): INR %.2f%n", allowance, excess, baggageFee);
	        System.out.printf("Tax (5%%)                              : INR %.2f%n", tax);
	        System.out.printf("Final Payable                         : INR %.2f%n", finalFare);
	        System.out.println("------------------------------------------------");

	        PaymentMode paymentMode = choosePaymentMode();
	        boolean paymentConfirmed = confirmPayment(finalFare, paymentMode);
	        if (!paymentConfirmed) {
	            System.out.println("Payment cancelled by user.");
	            return null;
	        }

	        flight.bookSeat(cabinClass, seatNo);

	        String pnr = flight.flightNo + "-" + (10000 + bookingsByPnr.size() + 1);
	        Booking booking = new Booking(pnr, passenger, flight, cabinClass, seatNo, meal, LocalDateTime.now());

	        booking.baseFare = baseFare;
	        booking.ageDiscountPercent = ageDiscountPercent;
	        booking.disabledDiscountPercent = disabledDiscountPercent;
	        booking.groupDiscountPercent = groupDiscountPercent;
	        booking.baggageWeight = baggageWeight;
	        booking.baggageFee = baggageFee;
	        booking.taxAmount = tax;
	        booking.finalFare = finalFare;
	        booking.paymentMode = paymentMode;
	        booking.paymentStatus = "SUCCESS";

	        return booking;
	    }

	  // save booking by PNR
	    private void saveBooking(Booking booking, String groupId) {
	        booking.groupId = groupId;
	        bookingsByPnr.put(booking.pnr, booking);

	        String key = booking.passenger.gmail.toLowerCase();
	        if (!bookingsByEmail.containsKey(key)) bookingsByEmail.put(key, new ArrayList<Booking>());
	        bookingsByEmail.get(key).add(booking);
	    }

	    private void cancelBooking() {
	        String pnr = readLine("Enter PNR to cancel booking: ").toUpperCase();
	        Booking b = bookingsByPnr.get(pnr);
	        if (b == null || b.status == BookingStatus.CANCELLED) {
	            System.out.println("Booking not found or already cancelled.");
	            return;
	        }

	        b.status = BookingStatus.CANCELLED;
	        b.flight.cancelSeat(b.cabinClass, b.seatNo);

	        double refund = b.finalFare * 0.90;
	        System.out.println("Booking cancelled successfully.");
	        System.out.printf("Refund Initiated (90%%): INR %.2f%n", refund);
	        System.out.println("Cancelled At: " + LocalDateTime.now().format(DT));
	    }

	    private void viewPassengerBookings() {
	        String email = readValidEmail("Enter passenger email: ").toLowerCase();
	        List<Booking> list = bookingsByEmail.get(email);	        
	        if (list == null || list.isEmpty()) {
	            System.out.println("❌ No bookings found.");
	            return;
	        }

	        System.out.println("\n========== PASSENGER BOOKINGS ==========");
	        for (Booking b : list) {
	            printBookingShort(b);
	            System.out.println("----------------------------------------");
	        }
	    }

	    private void printBookingShort(Booking b) {
	        System.out.println("PNR                : " + b.pnr);
	        System.out.println("Status             : " + b.status);
	        System.out.println("Passenger          : " + b.passenger.name + " (" + b.passenger.age + "/" + b.passenger.gender + ")");
	        System.out.println("Phone              : " + b.passenger.phone);
	        System.out.println("Passport           : " + b.passenger.passportNo + " (Exp: " + b.passenger.passportExpiry + ")");
	        System.out.println("Flight             : " + b.flight.flightNo + " (" + b.flight.origin + " -> " + b.flight.destination + ")");
	        System.out.println("Departure / Arrival: " + b.flight.departure.format(DT) + " / " + b.flight.arrival.format(DT));
	        System.out.println("Seat / Class       : " + b.seatNo + " / " + b.cabinClass);
	        System.out.println("Meal               : " + b.mealChoice);
	        System.out.printf("Fare               : INR %.2f%n", b.finalFare);
	    }

	    // ========================= CHECK-IN + UPGRADE =========================

	    private void passengerCheckInAndUpgrade() {
	        String pnr = readLine("Enter PNR for check-in: ").toUpperCase();
	        Booking b = bookingsByPnr.get(pnr);

	        if (b == null) {
	            System.out.println("PNR not found.");
	            return;
	        }
	        if (b.status == BookingStatus.CANCELLED) {
	            System.out.println("Cannot check-in. Booking is cancelled.");
	            return;
	        }

	        if (b.cabinClass == CabinClass.ECONOMY && b.flight.getAvailableSeats(CabinClass.BUSINESS) > 0) {
	            boolean wantUpgrade = readYesNo("Upgrade to Business available. Do you want upgrade? (Y/N): ");
	            if (wantUpgrade) {
	                String businessSeat = b.flight.getFirstAvailableBusinessSeat();
	                if (businessSeat != null) {
	                    double upgradeFee = b.baseFare * 0.75;
	                    System.out.printf("Upgrade Fee: INR %.2f%n", upgradeFee);

	                    if (confirmPayment(upgradeFee, b.paymentMode)) {
	                        b.flight.cancelSeat(CabinClass.ECONOMY, b.seatNo);
	                        b.flight.bookSeat(CabinClass.BUSINESS, businessSeat);
	                        b.cabinClass = CabinClass.BUSINESS;
	                        b.seatNo = businessSeat;
	                        b.finalFare += upgradeFee;
	                        b.upgraded = true;
	                        System.out.println("Upgrade successful. New seat: " + businessSeat);
	                    } else {
	                        System.out.println("Upgrade payment cancelled.");
	                    }
	                }
	            }
	        }

	        b.checkedIn = true;
	        System.out.println("Check-in completed.");
	        System.out.println("Boarding Group: " + (b.cabinClass == CabinClass.BUSINESS ? "A" : "B"));
	    }

	    // ========================= CREW  =========================

	    private void crewAndAirHostessManagement() {
	        System.out.println("\n1. View Crew");
	        System.out.println("2. Add Crew Member");
	        int choice = readInt("Choice: ");

	        if (choice == 1) {
	            System.out.printf(" %-6s %-16s %-8s %-20s %-10s %-5s%n", "Emp_ID","Name", "Gender", "Role", "Flight", "Experience");
	            System.out.println("-----------------------------------------------------------------");
	            for (CrewMember c : crewMembers) {
	                System.out.printf("%-16s %-8s %-20s %-10s %-5d%n",
	                       c.Emp_ID, c.name, c.gender, c.getRole(), c.assignedFlightNo, c.experienceYears);
	            }
	        } else if (choice == 2) {
	        	String Emp_ID = readNonEmpty("Employee ID: ");
	        	String name = readNonEmpty("Name: ");
	        	String gender = readNonEmpty("Gender: ");
	        	String assignedFlight = readLine("Assigned Flight No: ").toUpperCase();
	        	int experience = readIntInRange("Experience (years): ", 0, 50); 
	            System.out.println("Role: 1.Pilot 2.Co-Pilot 3.Air Hostess");
	            int role = readIntInRange("Select role: ", 1, 3);

	            CrewMember cm;
	            if (role == 3) cm = new AirHostess(Emp_ID,name, gender, assignedFlight, experience, "Premium");
	            else cm = new GeneralCrew(Emp_ID,name, gender, assignedFlight, experience, role == 1 ? "Pilot" : "Co-Pilot");

	            crewMembers.add(cm);
	            System.out.println("Crew member added.");
	        } else {
	            System.out.println("Invalid choice!");
	        }
	    }

	   
	    // ========================= MEAL / ENTERTAINMENT / ALERTS =========================

	    private void showMealMenu() {
	        System.out.println("\n================= IN-FLIGHT MEAL MENU =================");
	        System.out.println("Economy Class : Veg Combo | Chicken Rice | Noodles | Sandwich");
	        System.out.println("Business Class: Premium Veg Platter | Grilled Fish | Steak | Continental");
	        System.out.println("Special Meals : Kids Meal | Jain Meal | Diabetic Meal");
	        System.out.println("=======================================================");
	    }

	    private String chooseMealByClass(CabinClass cabinClass) {
	        if (cabinClass == CabinClass.BUSINESS) {
	            System.out.println("Business Meals: 1.Premium Veg 2.Grilled Fish 3.Jain Meal 4.Paneer Wrap");
	            int m = readIntInRange("Choice: ", 1, 4);
	            if (m == 2) return "Grilled Fish";
	            if (m == 3) return "Jain Meal";
	            if (m == 4) return "Paneer Wrap";
	            return "Premium Veg Platter";
	        } else {
	            System.out.println("Economy Meals: 1.Veg Combo 2.Chicken Rice 3.Noodles 4.Sandwich");
	            int m = readIntInRange("Choice: ", 1, 4);
	            if (m == 2) return "Chicken Rice";
	            if (m == 3) return "Noodles";
	            if (m == 4) return "Sandwich";
	            return "Veg Combo";
	        }
	    }
	    private void entertainmentSection() {
	        System.out.println("\n========== ENTERTAINMENT HUB ==========");
	        System.out.println("Current Time: " + LocalDateTime.now().format(DT));
	        System.out.println("1. Game: Sky Battle (25 mins)");
	        System.out.println("2. Game: Puzzle Quest (20 mins)");
	        System.out.println("3. Movie: Air Rescue (130 mins)");
	        System.out.println("4. Movie: Mission Wings (145 mins)");
	        int ch = readIntInRange("Select entertainment: ", 1, 4);

	        if (ch == 1) System.out.println("Launching Sky Battle. Enjoy!");
	        else if (ch == 2) System.out.println("Launching Puzzle Quest. Enjoy!");
	        else if (ch == 3) System.out.println("Now Playing: Air Rescue");
	        else System.out.println("Now Playing: Mission Wings");
	    }

	    private void submitFeedback() {
		    String name = readNonEmpty("Passenger Name: ");
		    String pnr = readLine("Enter PNR: ").toUpperCase();
		    Booking b = bookingsByPnr.get(pnr);

		    if (b == null || !b.passenger.name.equalsIgnoreCase(name)) {
		        System.out.println("Invalid PNR or Name.");
		        return;
		    }

		    String flightNo = b.flight.flightNo;
		    for (Feedback f : feedbacks)
		        if (f.flightNo.equalsIgnoreCase(flightNo) &&
		            f.passengerName.equalsIgnoreCase(name)) {
		            System.out.println("Feedback already submitted.");
		            return;
		        }

		    feedbacks.add(new Feedback(name, flightNo,
		        readIntInRange("Rating (1-5): ", 1, 5),
		        readNonEmpty("Comment: "),
		        LocalDateTime.now()));

		    System.out.println("Thank You.Feedback Recorded Successfully!!");
		}
	    // ========================= TICKET + PASSPORT =========================

	    private void printTicketAndPassport() {
	        String pnr = readLine("Enter PNR: ").toUpperCase();
	        Booking b = bookingsByPnr.get(pnr);
	        if (b == null) {
	            System.out.println("Booking not found.");
	            return;
	        }

	        printBoardingPass(b);
	        printPassportCard(b);
	    }

	    private void printBoardingPass(Booking b) {
	        Duration duration = Duration.between(b.flight.departure, b.flight.arrival);
	        long h = duration.toMinutes() / 60;
	        long m = duration.toMinutes() % 60;

	        System.out.println("\n+--------------------------------------------------------------------------------------+");
	        System.out.println("|                     SAFECIRCLE AIRLINES - OFFICIAL BOARDING PASS                    |");
	        System.out.println("+----------------------+-------------------------+-------------------------------------+");
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "PNR", b.pnr, "Status: " + b.status);
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Passenger", b.passenger.name, "Age/Gender: " + b.passenger.age + "/" + b.passenger.gender);
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Flight/Seat", b.flight.flightNo + " / " + b.seatNo, "Class: " + b.cabinClass);
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Route", b.flight.origin + " -> " + b.flight.destination, "Gate: " + b.flight.gateNo);
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Departure", b.flight.departure.format(DT), "Arrival: " + b.flight.arrival.format(DT));
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Duration", h + "h " + m + "m", "Meal: " + b.mealChoice);
	        System.out.printf("| %-20s | %-23s | %-35s |%n", "Check-in", b.checkedIn ? "COMPLETED" : "PENDING", "Upgraded: " + (b.upgraded ? "YES" : "NO"));
	        System.out.println("+----------------------+-------------------------+-------------------------------------+");
	        System.out.printf("| %-20s | %-23.2f | %-35s |%n", "Final Fare (INR)", b.finalFare, "Payment: " + b.paymentMode + " (" + b.paymentStatus + ")");
	        System.out.printf("| %-20s | %-23.2f | %-35s |%n", "Baggage (kg)", b.baggageWeight, "Baggage Fee: INR " + String.format("%.2f", b.baggageFee));
	        System.out.println("+--------------------------------------------------------------------------------------+");
	        System.out.println("| Report at gate at least 45 minutes before departure.                                |");
	        System.out.println("+--------------------------------------------------------------------------------------+");
	    }

	    private void printPassportCard(Booking b) {
	        Passenger p = b.passenger;
	        System.out.println("\n+======================================================================+");
	        System.out.println("|                    PASSENGER TRAVEL IDENTITY CARD                    |");
	        System.out.println("+======================================================================+");
	        System.out.printf("| Name             : %-50s |%n", p.name);
	        System.out.printf("| Nationality      : %-50s |%n", p.nationality);
	        System.out.printf("| Passport Number  : %-50s |%n", p.passportNo);
	        System.out.printf("| Passport Expiry  : %-50s |%n", p.passportExpiry);
	        System.out.printf("| Age / Gender     : %-50s |%n", p.age + " / " + p.gender);
	        System.out.printf("| Phone            : %-50s |%n", p.phone);
	        System.out.printf("| Email            : %-50s |%n", p.gmail);
	        System.out.printf("| Special Assist   : %-50s |%n", p.disabledPassenger ? "YES - " + p.assistanceNote : "NO");
	        System.out.printf("| Linked PNR       : %-50s |%n", b.pnr);
	        System.out.println("+======================================================================+");
	    }

	    // ========================= DASHBOARD =========================

	    private void operationsDashboard() {
	        int total = bookingsByPnr.size();
	        // Initialization
	        int active = 0;
	        int cancelled = 0;
	        int checkedIn = 0;
	        int upgraded = 0;
	        double revenue = 0.0;

	        for (Booking b : bookingsByPnr.values()) {
	            if (b.status == BookingStatus.CONFIRMED) {
	                active++;
	                revenue += b.finalFare;
	            } else {
	                cancelled++;
	            }
	            if (b.checkedIn) checkedIn++;
	            if (b.upgraded) upgraded++;
	        }

	        double avgRating = 0.0;
	        if (!feedbacks.isEmpty()) {
	            int sum = 0;
	            for (Feedback f : feedbacks) sum += f.rating;
	            avgRating = (double) sum / feedbacks.size();
	        }

	        System.out.println("\n================= OPERATIONS DASHBOARD =================");
	        System.out.println("Date                  : " + LocalDate.now());
	        System.out.println("Total Flights         : " + flights.size());
	        System.out.println("Total Bookings        : " + total);
	        System.out.println("Active Bookings       : " + active);
	        System.out.println("Cancelled Bookings    : " + cancelled);
	        System.out.println("Checked-in Passengers : " + checkedIn);
	        System.out.println("Upgraded Passengers   : " + upgraded);
	        System.out.println("Group Bookings        : " + groupBookings.size());
	        System.out.printf("Revenue (Active)      : INR %.2f%n", revenue);
	        System.out.printf("Average Feedback      : %.2f / 5.00%n", avgRating);
	        System.out.println("========================================================");
	    }

	    // ========================= PRICING =========================
	    //abstraction

	    private double calculateDynamicBaseFare(Flight flight, CabinClass cabinClass) {
	        double base;//starting price of ticket
	        String dest = flight.destination.toLowerCase();

	        if ("dubai".equals(dest)) base = 18500;//initial ticket price based on destination
	        else if ("mumbai".equals(dest) || "delhi".equals(dest) || "chennai".equals(dest) || "bengaluru".equals(dest)) base = 5500;
	        else base = 7000;

	        if (cabinClass == CabinClass.BUSINESS) base *= 2.0;//if business class price is twice

	        int total = flight.getTotalSeats(cabinClass);
	        int booked = total - flight.getAvailableSeats(cabinClass);
	        double occupancy = total == 0 ? 0.0 : ((double) booked / total);

	        double demandFactor;//price changes according to availability of seats
	        if (occupancy >= 0.80) demandFactor = 1.30;
	        else if (occupancy >= 0.60) demandFactor = 1.20;
	        else if (occupancy >= 0.40) demandFactor = 1.10;
	        else demandFactor = 1.00;

	        long hoursToDeparture = Duration.between(LocalDateTime.now(), flight.departure).toHours();
	        double urgencyFactor = hoursToDeparture <= 6 ? 1.15 : 1.00;//according to urgency price increases

	        return base * demandFactor * urgencyFactor;
	    }

	    private double getAgeDiscountPercent(int age) {
	        if (age < 12) return 50.0;
	        if (age >= 60) return 30.0;
	        return 0.0;
	    }

	    // ========================= INPUT VALIDATIONS =========================

	    private Passenger readPassengerDetails() {
	        System.out.println("\nEnter Passenger Details:");

	        String name = readNonEmpty("Name: ");
	        String gender = readNonEmpty("Gender: ");
	        int age = readIntInRange("Age: ", 1, 110);
	        String email = readValidEmail("Email: ");
	        String phone = readValidPhone("Phone (10 digits): ");
	        String nationality = readNonEmpty("Nationality: ");
	        String passportNo = readValidPassport("Passport Number: ");

	        LocalDate minRequiredDate = LocalDate.now().plusMonths(6);
	        LocalDate passportExpiry = readPassportDateMin("Passport Expiry (yyyy-MM-dd): ", minRequiredDate);

	        boolean disabled = readYesNo("Disabled passenger? (Y/N): ");
	        String assistance = disabled ? readNonEmpty("Assistance details: ") : "None";

	        return new Passenger(name, gender, age, email, phone, nationality, passportNo, passportExpiry, disabled, assistance);
	    }
	    			//validations
	    private String readValidEmail(String prompt) {
	        while (true) {
	            String email = readLine(prompt);
	            if (EMAIL_PATTERN.matcher(email).matches()) return email;
	            System.out.println("Invalid email format.");
	        }
	    }

	    private String readValidPhone(String prompt) {
	        while (true) {
	            String phone = readLine(prompt);
	            if (PHONE_PATTERN.matcher(phone).matches()) return phone;
	            System.out.println("Phone number must be exactly 10 digits.");
	        }
	    }

	    private String readValidPassport(String prompt) {
	        while (true) {
	            String p = readLine(prompt).toUpperCase();
	            if (PASSPORT_PATTERN.matcher(p).matches()) return p;
	            System.out.println("Passport must be 6 to 12 alphanumeric characters.");
	        }
	    }

	    private LocalDate readPassportDateMin(String prompt, LocalDate minDate) {
	        while (true) {
	            String text = readLine(prompt);
	            //exception handling
	            try {
	                LocalDate date = LocalDate.parse(text);
	                if (date.isBefore(minDate)) {
	                    System.out.println("Passport must be valid at least until " + minDate + " (minimum 6 months from booking date).");
	                    continue;
	                }
	                return date;
	            } catch (Exception e) {
	                System.out.println("Invalid date format. Use yyyy-MM-dd.");
	            }
	        }
	    }

	    private String readValidSeatNumber(String prompt, CabinClass cabinClass) {
	        while (true) {
	            String seat = readLine(prompt).toUpperCase();

	            if (!seat.matches("^[EB]\\d{2}$")) {
	                System.out.println("Invalid seat format. Use E01 or B02.");
	                continue;
	            }
	            if (cabinClass == CabinClass.ECONOMY && !seat.startsWith("E")) {
	                System.out.println("Economy seats must start with E.");
	                continue;
	            }
	            if (cabinClass == CabinClass.BUSINESS && !seat.startsWith("B")) {
	                System.out.println("Business seats must start with B.");
	                continue;
	            }
	            return seat;
	        }
	    }

	    private CabinClass chooseCabinClass() {
	        while (true) {
	            int c = readInt("Select Class: 1.Economy  2.Business : ");
	            if (c == 1) return CabinClass.ECONOMY;
	            if (c == 2) return CabinClass.BUSINESS;
	            System.out.println("Invalid class choice.");
	        }
	    }

	    private PaymentMode choosePaymentMode() {
	        while (true) {
	            int p = readInt("Payment Mode: 1.UPI  2.Card  3.NetBanking  4.Cash : ");
	            if (p == 1) return PaymentMode.UPI;
	            if (p == 2) return PaymentMode.CARD;
	            if (p == 3) return PaymentMode.NETBANKING;
	            if (p == 4) return PaymentMode.CASH;
	            System.out.println("Invalid payment mode.");
	        }
	    }

	    private boolean confirmPayment(double amount, PaymentMode mode) {
	        System.out.printf("Confirm payment of INR %.2f via %s?%n", amount, mode);
	        return readYesNo("Proceed with payment? (Y/N): ");
	    }

	    private String readNonEmpty(String prompt) {
	        while (true) {
	            String s = readLine(prompt);
	            if (s.length() > 0) return s;
	            System.out.println("This field cannot be empty.");
	        }
	    }

	    private int readIntInRange(String prompt, int min, int max) {
	        while (true) {
	            int v = readInt(prompt);
	            if (v >= min && v <= max) return v;
	            System.out.println("Enter value between " + min + " and " + max + ".");	//go according to menu
	        }
	    }
	    private double readDoubleNonNegative(String prompt) {
	        while (true) {
	            double v = readDouble(prompt);
	            if (v >= 0) return v;
	            System.out.println("Value cannot be negative.");
	        }
	    }

	    private String readLine(String prompt) {
	        System.out.print(prompt);
	        return SC.nextLine().trim();//for removing extra space
	    }
//exception handling
	    private int readInt(String prompt) {
	        while (true) {
	        	//exception handling
	            try {
	                System.out.print(prompt);
	                return Integer.parseInt(SC.nextLine().trim());
	            } catch (Exception e) {
	                System.out.println("Please enter a valid integer.");
	            }
	        }
	    }

	    private double readDouble(String prompt) {
	        while (true) {
	            try {
	                System.out.print(prompt);
	                return Double.parseDouble(SC.nextLine().trim());
	            } catch (Exception e) {
	                System.out.println("Please enter a valid decimal number.");
	            }
	        }
	    }

	    private boolean readYesNo(String prompt) {
	        while (true) {
	            String s = readLine(prompt).toUpperCase();
	            if ("Y".equals(s)) return true;
	            if ("N".equals(s)) return false;
	            System.out.println("Enter Y or N.");//must enter Y/N
	        }
	    }

	    // ========================= SEED DATA =========================

	    private void seedData() {
	        addFlight("AI101", "Delhi", "Mumbai", 6, 8, 36, 12, "A1", "On Time", "Clear");
	        addFlight("AI202", "Mumbai", "Bengaluru", 9, 11, 35, 10, "B2", "Boarding Soon", "Light Rain");
	        addFlight("AI303", "Chennai", "Kolkata", 7, 10, 38, 8, "C3", "On Time", "Clear");
	        addFlight("AI404", "Hyderabad", "Delhi", 5, 7, 42, 14, "D4", "Delayed 20m", "Cloudy");
	        addFlight("AI505", "Pune", "Jaipur", 10, 12, 30, 10, "E5", "On Time", "Clear");
	        addFlight("AI606", "Ahmedabad", "Goa", 8, 10, 33, 9, "F6", "On Time", "Windy");
	        addFlight("AI707", "Lucknow", "Delhi", 4, 5, 45, 15, "G7", "On Time", "Clear");
	        addFlight("AI808", "Nagpur", "Mumbai", 3, 4, 36, 10, "H8", "Boarding Soon", "Haze");
	        addFlight("AI909", "Kochi", "Chennai", 11, 12, 34, 8, "I9", "On Time", "Rain");
	        addFlight("AI111", "Indore", "Bengaluru", 13, 15, 37, 11, "J1", "On Time", "Clear");
	        addFlight("AI222", "Delhi", "Dubai", 15, 20, 50, 20, "K2", "On Time", "Clear");

	        crewMembers.add(new GeneralCrew("C101","Arjun Mehta", "Male", "AI101", 12, "Pilot"));
	        crewMembers.add(new GeneralCrew("C102","Riya Sen", "Female", "AI101", 8, "Co-Pilot"));
	        crewMembers.add(new AirHostess("C103","Neha Sharma", "Female", "AI202", 6, "Business"));
	        crewMembers.add(new AirHostess("C104","Sara Khan", "Female", "AI303", 5, "Economy"));

	    }

	    private void addFlight(String no, String origin, String destination, int depAfterHours, int arrAfterHours,
	                           int ecoSeats, int businessSeats, String gate, String status, String weather) {
	        LocalDateTime now = LocalDateTime.now();
	        flights.put(no, new Flight(no, origin, destination,
	                now.plusHours(depAfterHours), now.plusHours(arrAfterHours),
	                ecoSeats, businessSeats, gate, status, weather));
	    }

	    // ========================= MODELS =========================

	    enum CabinClass { ECONOMY, BUSINESS }

	    enum BookingStatus { CONFIRMED, CANCELLED }

	    enum PaymentMode { UPI, CARD, NETBANKING, CASH }

	    static class Passenger {
	        private final String name;
	        private final String gender;
	        private final int age;
	        private final String gmail;
	        private final String phone;
	        private final String nationality;
	        private final String passportNo;
	        private final LocalDate passportExpiry;
	        private final boolean disabledPassenger;
	        private final String assistanceNote;
	        	//parameterized constructor
	        Passenger(String name, String gender, int age, String gmail, String phone, String nationality,
	                  String passportNo, LocalDate passportExpiry, boolean disabledPassenger, String assistanceNote) {
	            this.name = name;
	            this.gender = gender;		//Instance variables
	            this.age = age;
	            this.gmail = gmail;
	            this.phone = phone;
	            this.nationality = nationality;
	            this.passportNo = passportNo;
	            this.passportExpiry = passportExpiry;
	            this.disabledPassenger = disabledPassenger;
	            this.assistanceNote = assistanceNote;
	        }
	    }

	    static class Flight {
	        private final String flightNo;
	        private final String origin;
	        private final String destination;
	        private final LocalDateTime departure;
	        private final LocalDateTime arrival;
	        private final String gateNo;
	        private final String status;
	        private final String weatherNote;
	        	//java collection framework 
	        private final LinkedHashMap<String, Boolean> ecoSeatMap = new LinkedHashMap<String, Boolean>();
	        private final LinkedHashMap<String, Boolean> businessSeatMap = new LinkedHashMap<String, Boolean>();

	        private final Set<String> disabledEcoSeats = new LinkedHashSet<String>();
	        private final Set<String> disabledBusinessSeats = new LinkedHashSet<String>();
	        	//parameterized constructor
	        Flight(String flightNo, String origin, String destination, LocalDateTime departure, LocalDateTime arrival,
	               int ecoSeats, int businessSeats, String gateNo, String status, String weatherNote) {
	            this.flightNo = flightNo;
	            this.origin = origin;
	            this.destination = destination;
	            this.departure = departure;
	            this.arrival = arrival;
	            this.gateNo = gateNo;
	            this.status = status;
	            this.weatherNote = weatherNote;

	            for (int i = 1; i <= ecoSeats; i++) ecoSeatMap.put("E" + String.format("%02d", i), false);//E01
	            for (int i = 1; i <= businessSeats; i++) businessSeatMap.put("B" + String.format("%02d", i), false);//B01
	            	//disabled person first 5 seats are reserved
	            for (int i = 1; i <= Math.min(5, ecoSeats); i++) {
	                disabledEcoSeats.add("E" + String.format("%02d", i));
	            }
	            for (int i = 1; i <= Math.min(5, businessSeats); i++) {
	                disabledBusinessSeats.add("B" + String.format("%02d", i));
	            }
	        }
	        		//return total seats of  a class
	        int getTotalSeats(CabinClass c) {
	            return c == CabinClass.ECONOMY ? ecoSeatMap.size() : businessSeatMap.size();
	        }
	        //available seats
	        int getAvailableSeats(CabinClass c) {
	            Map<String, Boolean> map = c == CabinClass.ECONOMY ? ecoSeatMap : businessSeatMap;
	            int count = 0;
	            for (Boolean b : map.values()) if (!b) count++;//true = booked and false =available
	            return count;
	        }

	        boolean isSeatAvailable(CabinClass c, String seatNo) {
	            Map<String, Boolean> map = c == CabinClass.ECONOMY ? ecoSeatMap : businessSeatMap;
	            return map.containsKey(seatNo) && !map.get(seatNo);//if both true seat is available
	        }

	        void bookSeat(CabinClass c, String seatNo) {
	            Map<String, Boolean> map = c == CabinClass.ECONOMY ? ecoSeatMap : businessSeatMap;
	            map.put(seatNo, true);// true =mark seat as book
	        }

	        void cancelSeat(CabinClass c, String seatNo) {
	            Map<String, Boolean> map = c == CabinClass.ECONOMY ? ecoSeatMap : businessSeatMap;
	            if (map.containsKey(seatNo)) map.put(seatNo, false);//false = available
	        }

	        boolean isDisabledSeat(String seatNo, CabinClass cabinClass) {
	            Set<String> set = cabinClass == CabinClass.ECONOMY ? disabledEcoSeats : disabledBusinessSeats;
	            return set.contains(seatNo);
	        }

	        List<String> getAvailableDisabledSeats(CabinClass cabinClass) {
	            List<String> out = new ArrayList<String>();
	            if (cabinClass == CabinClass.ECONOMY) {
	                for (String s : disabledEcoSeats) {
	                    if (ecoSeatMap.containsKey(s) && !ecoSeatMap.get(s)) out.add(s);
	                }
	            } else {
	                for (String s : disabledBusinessSeats) {
	                    if (businessSeatMap.containsKey(s) && !businessSeatMap.get(s)) out.add(s);
	                }
	            }
	            return out;
	        }

	        boolean hasNormalSeatAvailable(CabinClass cabinClass) {
	            if (cabinClass == CabinClass.ECONOMY) {
	                for (Map.Entry<String, Boolean> e : ecoSeatMap.entrySet()) {
	                    if (!disabledEcoSeats.contains(e.getKey()) && !e.getValue()) return true;
	                }
	            } else {
	                for (Map.Entry<String, Boolean> e : businessSeatMap.entrySet()) {
	                    if (!disabledBusinessSeats.contains(e.getKey()) && !e.getValue()) return true;
	                }
	            }
	            return false;
	        }

	        String getFirstAvailableBusinessSeat() {
	            for (Map.Entry<String, Boolean> e : businessSeatMap.entrySet()) {
	                if (!e.getValue()) return e.getKey();//key = seat no.
	            }
	            return null;
	        }

	        void printSeatPattern(CabinClass cabinClass, int seatsPerRow) {
	            Map<String, Boolean> map = cabinClass == CabinClass.ECONOMY ? ecoSeatMap : businessSeatMap;
	            Set<String> disabledSet = cabinClass == CabinClass.ECONOMY ? disabledEcoSeats : disabledBusinessSeats;

	            int i = 0;
	            for (Map.Entry<String, Boolean> e : map.entrySet()) {
	                String seat = e.getKey();
	                String mark = e.getValue() ? "X" : "O";//O = available X = booked

	                if (disabledSet.contains(seat)) System.out.printf("%s[D%s]  ", seat, mark);
	                else System.out.printf("%s[%s]   ", seat, mark);//for disable person

	                i++;
	                if (i % seatsPerRow == 0) System.out.println();//seats per row
	            }
	            System.out.println();
	        }
	    }

	    static class Booking {
	        private final String pnr;
	        private final Passenger passenger;
	        private final Flight flight;
	        private CabinClass cabinClass;
	        private String seatNo;
	        private final String mealChoice;
	        private final LocalDateTime bookedAt;

	        private BookingStatus status = BookingStatus.CONFIRMED;
	        private PaymentMode paymentMode;
	        private String paymentStatus = "PENDING";
	        private boolean checkedIn = false;
	        private boolean upgraded = false;
	        private String groupId;

	        private double baseFare;
	        private double ageDiscountPercent;
	        private double disabledDiscountPercent;
	        private double groupDiscountPercent;
	        private double baggageWeight;
	        private double baggageFee;
	        private double taxAmount;
	        private double finalFare;
//parameterized constructor
	        Booking(String pnr, Passenger passenger, Flight flight, CabinClass cabinClass, String seatNo,
	                String mealChoice, LocalDateTime bookedAt) {
	            this.pnr = pnr;
	            this.passenger = passenger;
	            this.flight = flight;
	            this.cabinClass = cabinClass;
	            this.seatNo = seatNo;
	            this.mealChoice = mealChoice;
	            this.bookedAt = bookedAt;
	        }
	    }
//abstract class crewmember
	    static abstract class CrewMember {
	        protected final String name;
	        protected final String gender;
	        protected final String assignedFlightNo;
	        protected final int experienceYears;
	        protected final String Emp_ID;

	        CrewMember( String Emp_ID,String name, String gender, String assignedFlightNo, int experienceYears) {
	        	this.Emp_ID=Emp_ID;
	            this.name = name;
	            this.gender = gender;
	            this.assignedFlightNo = assignedFlightNo;
	            this.experienceYears = experienceYears;
	        }

	        abstract String getRole();
	    }
    //by overriden methods
	    static class GeneralCrew extends CrewMember {
	        private final String role;
	        GeneralCrew( String Emp_ID,String name, String gender, String assignedFlightNo, int expYears, String role) {
	            super( Emp_ID,name, gender, assignedFlightNo, expYears);
	            this.role = role;
	        }

	        String getRole() {
	            return role;
	        }
	    }

	    static class AirHostess extends CrewMember {
	        private final String serviceClass;

	        AirHostess( String Emp_ID,String name, String gender, String assignedFlightNo, int expYears, String serviceClass) {
	            super( Emp_ID,name, gender, assignedFlightNo, expYears);
	            this.serviceClass = serviceClass;
	        }

	        String getRole() {
	            return "Air Hostess (" + serviceClass + ")";
	        }
	    }
            static class Feedback {
	        private final String passengerName;
	        private final String flightNo;
	        private final int rating;
	        private final String comment;
	        private final LocalDateTime time;

	        Feedback(String passengerName, String flightNo, int rating, String comment, LocalDateTime time) {
	            this.passengerName = passengerName;
	            this.flightNo = flightNo;
	            this.rating = rating;
	            this.comment = comment;
	            this.time = time;
	        }
	    }

	    static class AlertCenter {
	        void showSafetyAlerts() {
	            System.out.println("\n============== SAFETY ALERTS ==============");
	            System.out.println("1. Fasten your seatbelt while seated.");
	            System.out.println("2. Keep electronic devices in flight mode.");
	            System.out.println("3. Locate nearest emergency exit.");
	            System.out.println("4. Follow cabin crew instructions.");
	            System.out.println("5. Keep aisle clear.");
	            System.out.println("===========================================");
	        }

	        void showEmergencyAlerts() {
	            System.out.println("\n============ EMERGENCY ALERTS ============");
	            System.out.println("ALERT LEVEL: GREEN");
	            System.out.println("- Medical Emergency: Inform cabin crew immediately.");
	            System.out.println("- Fire/Smoke: Follow exit lights and crew instructions.");
	            System.out.println("- Security Threat: Stay calm and follow announcements.");
	            System.out.println("- Helpline: +91-1800-ALERT-911");
	            System.out.println("==========================================");
	        }
	    }
	}

