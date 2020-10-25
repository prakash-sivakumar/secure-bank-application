package com.example.banking.bank_app.controller;

import com.example.banking.bank_app.model.*;
import com.example.banking.bank_app.respository.AuthUserRepository;
import com.example.banking.bank_app.service.AccountRequestService;
import com.example.banking.bank_app.service.EmployeeService;
import com.example.banking.bank_app.service.LogService;
import com.example.banking.bank_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping(value="/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private AccountRequestService accountRequestService;

    @RequestMapping(value="/list/{page}", method= RequestMethod.GET)
    public ModelAndView list(@PathVariable("page") int page, Authentication authentication, @ModelAttribute("message") String message) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        ModelAndView modelAndView;
        int tier;
        if(roles.contains("ADMIN")){
            modelAndView = new ModelAndView("employee_admin");
            tier = -1; // so that admin can view all accounts
        }else{
            modelAndView = new ModelAndView("employee_list");
            tier = Config.ADMIN; // tier2 can't view admin's details
        }
        PageRequest pageable = PageRequest.of(page - 1, 15);
        Page<Employee> employeePage = employeeService.getPaginated(pageable, tier);
        int totalPages = employeePage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1,totalPages).boxed().collect(Collectors.toList());
            modelAndView.addObject("pageNumbers", pageNumbers);
        }
        modelAndView.addObject("activeEmployeeList", true);
        modelAndView.addObject("EmployeeList", employeePage.getContent());
        Employee employee= new Employee();
        modelAndView.addObject("employee", employee);
        modelAndView.addObject("message", message);
        return modelAndView;
    }


    @RequestMapping(value="/get/{employee_id}", method= RequestMethod.GET)
    public Employee getEmployee(@PathVariable("employee_id") Long employee_id) {
        return employeeService.getEmployeeById(employee_id);
    }

    @RequestMapping(value="/edit", method= RequestMethod.POST) //admin
    public ModelAndView editEmployee(@Valid Employee employee, BindingResult bindingResult,Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/employee/list/1");
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        if (roles.contains("ADMIN")) {
            Employee newEmployee = employeeService.getEmployeeById(employee.getEmployee_id());
            if(newEmployee.getTier_level() == Config.ADMIN && employee.getTier_level() != Config.ADMIN){
                redirectAttributes.addFlashAttribute("message","Cannot change tier level of Admin!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(!employee.getEmployee_id().equals(newEmployee.getEmployee_id()) || !employee.getEmail_id().equals(newEmployee.getEmail_id()) | !employee.getEmployee_name().equals(newEmployee.getEmployee_name())){
                redirectAttributes.addFlashAttribute("message","Cannot edit Id, name & Email!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(!employee.getGender().equals("M")&&!employee.getGender().equals("F")){
                redirectAttributes.addFlashAttribute("message","Invalid Gender!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(employee.getAge() <0|| employee.getAge() > 150){
                redirectAttributes.addFlashAttribute("message","Age should be between 1 and 150!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            try{
                Long id11 = employeeService.findUserByPhone(employee.getContact_no());
                if(id11 != null && !id11.equals(employee.getEmployee_id())){
                    redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                    return new ModelAndView("redirect:/employee/list/1");
                }
            }
            catch (Exception e){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(employee.getContact_no() == null || !employee.getContact_no().matches("-?\\d+(\\.\\d+)?") || employee.getContact_no().length() != 10){
                redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            employee.setCreated(newEmployee.getCreated());
            employee.setUpdated(new Timestamp(System.currentTimeMillis()));
            employeeService.saveOrUpdate(employee);
            logService.saveLog(authentication.getName(),"Changed employee profile for "+employee.getEmail_id());
            redirectAttributes.addFlashAttribute("message","Successfully saved!");
        }
        return new ModelAndView("redirect:/employee/list/1");
    }

    @RequestMapping(value="/edit1", method= RequestMethod.POST)//tier2 homepage
    public ModelAndView editEmployee1(@Valid Employee employee, BindingResult bindingResult, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/tier2");
        }
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        String name = employeeService.getEmployeeById(userId).getEmployee_name();
        Employee newEmployee = employeeService.getEmployeeById(employee.getEmployee_id());
        if(!employee.getEmployee_id().equals(newEmployee.getEmployee_id()) || !employee.getEmail_id().equals(newEmployee.getEmail_id()) | !employee.getEmployee_name().equals(newEmployee.getEmployee_name())){
            redirectAttributes.addFlashAttribute("message","Cannot edit Id, name & Email!");
            return new ModelAndView("redirect:/tier2");
        }
        if(!employee.getGender().equals("M")&&!employee.getGender().equals("F")){
            redirectAttributes.addFlashAttribute("message","Invalid Gender!");
            return new ModelAndView("redirect:/tier2");
        }
        if(employee.getAge() <0|| employee.getAge() > 150){
            redirectAttributes.addFlashAttribute("message","Age should be between 1 and 150!");
            return new ModelAndView("redirect:/tier2");
        }
        try{
            Long id11 = employeeService.findUserByPhone(employee.getContact_no());
            if(id11 != null && !id11.equals(employee.getEmployee_id())){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return new ModelAndView("redirect:/tier2");
            }
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
            return new ModelAndView("redirect:/tier2");
        }
        if(employee.getContact_no() == null || !employee.getContact_no().matches("-?\\d+(\\.\\d+)?") || employee.getContact_no().length() != 10){
            redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
            return new ModelAndView("redirect:/tier2");
        }
        AccountRequest accountRequest = new AccountRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("employee_id", employee.getEmployee_id());
        attributes.put("employee_name",employee.getEmployee_name());
        attributes.put("gender", employee.getGender());
        attributes.put("age", employee.getAge());
        attributes.put("tier_level", newEmployee.getTier_level());
        attributes.put("designation_id", newEmployee.getDesignation_id());
        attributes.put("contact_no", employee.getContact_no());
        attributes.put("email_id", employee.getEmail_id());
        attributes.put("address", employee.getAddress());
        accountRequest.setDescription("Edit Account for" +name+" : "+employee.getEmployee_name());
        accountRequest.setEmployee(attributes);
        accountRequest.setCreated_by(name);
        accountRequest.setType(Config.EMPLOYEE_TYPE);
        if (roles.contains("ADMIN")){
            accountRequest.setRole(Config.ADMIN);
            accountRequest.setStatus_id(Config.APPROVED);
        }else{
            accountRequest.setRole(Config.ADMIN);
            accountRequest.setStatus_id(Config.PENDING);
        }
        accountRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
        try {
            accountRequest.serializeemployee();
        }
        catch(Exception e){
            System.out.println("Exception");
        }
        accountRequestService.saveOrUpdate(accountRequest);
        if(roles.contains("ADMIN")){
            redirectAttributes.addFlashAttribute("message","Successfully saved!");
        }
        else{
            redirectAttributes.addFlashAttribute("message","Successfully saved, pending approval!");
        }
        logService.saveLog(authentication.getName(),"Changed employee profile for "+employee.getEmail_id());
        return new ModelAndView("redirect:/tier2");
    }

    @RequestMapping(value="/edit2", method= RequestMethod.POST)//tier2 employee page
    public ModelAndView editEmployee2(@Valid Employee employee, BindingResult bindingResult,Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/employee/list/1");
        }
        Long userId =  employeeService.findUserByEmail(authentication.getName());
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        String name = employeeService.getEmployeeById(userId).getEmployee_name();
        Employee newEmployee = employeeService.getEmployeeById(employee.getEmployee_id());
        if(!employee.getEmployee_id().equals(newEmployee.getEmployee_id()) || !employee.getEmail_id().equals(newEmployee.getEmail_id()) | !employee.getEmployee_name().equals(newEmployee.getEmployee_name())){
            redirectAttributes.addFlashAttribute("message","Cannot edit Id, name & Email!");
            return new ModelAndView("redirect:/employee/list/1");
        }
        try{
            Long id11 = employeeService.findUserByPhone(employee.getContact_no());
            if(id11 != null && !id11.equals(employee.getEmployee_id())){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return new ModelAndView("redirect:/employee/list/1");
            }
        }
        catch (Exception e){
            redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
            return new ModelAndView("redirect:/employee/list/1");
        }
        if(!employee.getGender().equals("M")&&!employee.getGender().equals("F")){
            redirectAttributes.addFlashAttribute("message","Invalid Gender!");
            return new ModelAndView("redirect:/employee/list/1");
        }
        if(employee.getAge() <0|| employee.getAge() > 150){
            redirectAttributes.addFlashAttribute("message","Age should be between 1 and 150!");
            return new ModelAndView("redirect:/employee/list/1");
        }
        if(employee.getContact_no() == null || !employee.getContact_no().matches("-?\\d+(\\.\\d+)?") || employee.getContact_no().length() != 10){
            redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
            return new ModelAndView("redirect:/employee/list/1");
        }
        AccountRequest accountRequest = new AccountRequest();
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("employee_id", employee.getEmployee_id());
        attributes.put("employee_name",employee.getEmployee_name());
        attributes.put("gender", employee.getGender());
        attributes.put("age", employee.getAge());
        attributes.put("tier_level", newEmployee.getTier_level());
        attributes.put("designation_id", newEmployee.getDesignation_id());
        attributes.put("contact_no", employee.getContact_no());
        attributes.put("email_id", employee.getEmail_id());
        attributes.put("address", employee.getAddress());
        accountRequest.setDescription("Edit Profile for" +name+" : "+employee.getEmployee_name());
        accountRequest.setEmployee(attributes);
        accountRequest.setCreated_by(name);
        accountRequest.setType(Config.EMPLOYEE_TYPE);
        if (roles.contains("ADMIN")){
            accountRequest.setRole(Config.ADMIN);
            accountRequest.setStatus_id(Config.APPROVED);
        }else{
            accountRequest.setRole(Config.ADMIN);
            accountRequest.setStatus_id(Config.PENDING);
        }
        accountRequest.setCreated_at(new Timestamp(System.currentTimeMillis()));
        try {
            accountRequest.serializeemployee();
        }
        catch(Exception e){
            System.out.println("Exception");
        }
        accountRequestService.saveOrUpdate(accountRequest);
        if(roles.contains("ADMIN")){
            redirectAttributes.addFlashAttribute("message","Successfully saved!");
        }
        else{
            redirectAttributes.addFlashAttribute("message","Successfully saved, pending approval!");
        }
        logService.saveLog(authentication.getName(),"Changed employee profile for "+employee.getEmail_id());
        return new ModelAndView("redirect:/employee/list/1");
    }

    @RequestMapping(value="/delete/{employee}", method= RequestMethod.POST)
    public ModelAndView deleteAccount(@PathVariable("employee") Long employeeId, Authentication authentication) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        Auth_user auth_user = userService.findByEmail(employee.getEmail_id());
        AuthUserRole authUserRole = new AuthUserRole();
        authUserRole.setAuth_user_id(new Long(auth_user.getId()));
        authUserRole.setAuth_role_id(new Long(employee.getTier_level()));
        userService.deleteAuthUserRole(authUserRole);
        userService.deleteAuthUser(auth_user.getId());
        employeeService.deleteEmployee(employeeId);
        logService.saveLog(authentication.getName(),"Deleted employee profile of "+employeeId);
        return new ModelAndView("redirect:/employee/list/1");
    }


    @RequestMapping(value="/create", method= RequestMethod.POST) //admin
    public ModelAndView createEmployee(@Valid Employee employee, BindingResult bindingResult,Authentication authentication, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("message","Please fix the errors");
            return new ModelAndView("redirect:/employee/list/1");
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<String>();
        for(GrantedAuthority a : authorities) {
            roles.add(a.getAuthority());
        }
        if (roles.contains("ADMIN")) {
            Auth_user auth_user = new Auth_user();
            auth_user.setEmail(employee.getEmail_id());
            if(userService.userAlreadyExist(auth_user)){
                redirectAttributes.addFlashAttribute("message","Email already exists!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(!checkvalidPassword(employee.getPassword())){
                redirectAttributes.addFlashAttribute("message","Password doesnt match requirements!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            try{
                if(employeeService.findUserByPhone(employee.getContact_no())!=null){
                    redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                    return new ModelAndView("redirect:/employee/list/1");
                }
            }
            catch (Exception e){
                redirectAttributes.addFlashAttribute("message","Contact Number already exists!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(!employee.getGender().equals("M")&&!employee.getGender().equals("F")){
                redirectAttributes.addFlashAttribute("message","Invalid Gender!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(employee.getAge() <0|| employee.getAge() > 150){
                redirectAttributes.addFlashAttribute("message","Age should be between 1 and 150!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            if(employee.getContact_no() == null || !employee.getContact_no().matches("-?\\d+(\\.\\d+)?") || employee.getContact_no().length() != 10){
                redirectAttributes.addFlashAttribute("message","Contact Number not valid!");
                return new ModelAndView("redirect:/employee/list/1");
            }
            employee.setCreated(new Timestamp(System.currentTimeMillis()));
            employee.setUpdated(new Timestamp(System.currentTimeMillis()));
            employeeService.saveOrUpdate(employee);
            auth_user.setName(employee.getEmployee_name());
            auth_user.setPassword(employee.getPassword());
            auth_user.setLastName("");
            Auth_user newAuthUser = userService.saveUser(auth_user);

            AuthUserRole authUserRole = new AuthUserRole();
            authUserRole.setAuth_user_id(new Long(newAuthUser.getId()));
            authUserRole.setAuth_role_id(new Long(employee.getTier_level()));
            userService.save(authUserRole);
            redirectAttributes.addFlashAttribute("message","Successfully saved!");
            logService.saveLog(authentication.getName(),"Created new employee profile for "+employee.getEmail_id());
        }
        return new ModelAndView("redirect:/employee/list/1");
    }

    public boolean checkvalidPassword(String password) {
        String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        return password.matches(pattern);
    }

}
