package com.green.yp.message.data.repository;

import com.green.yp.message.data.model.MessageMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageMetaRepository  extends JpaRepository<MessageMeta, UUID> {}
