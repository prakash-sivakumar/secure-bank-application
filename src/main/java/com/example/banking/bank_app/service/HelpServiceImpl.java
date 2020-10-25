package com.example.banking.bank_app.service;

import com.example.banking.bank_app.model.Help;
import com.example.banking.bank_app.respository.HelpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpServiceImpl implements HelpService {

    @Autowired
    private HelpRepository helpRepository;

    @Override
    public List<Help> getAllHelp()
    {
        return (List<Help>) helpRepository.findAll();
    }

    @Override
    public Help getCheckByID(Long helpid) {
        return helpRepository.findById(helpid).get();
    }

    @Override
    public void saveOrUpdate(Help help) {
        helpRepository.save(help);
    }

    @Override
    public void deleteHelp(Long helpid) {
        helpRepository.deleteById(helpid);
    }


    @Override
    public Page<Help> getPaginated(Pageable pageable) {
        return helpRepository.findAll(pageable);
    }


}
