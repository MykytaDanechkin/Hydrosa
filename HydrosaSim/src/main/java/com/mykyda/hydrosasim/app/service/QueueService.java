package com.mykyda.hydrosasim.app.service;

import com.mykyda.hydrosasim.app.data.entity.Signal;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {

    private final BlockingQueue<Signal> queue =
            new LinkedBlockingQueue<>(1000);

    public void enqueue(Signal signal) {
        queue.offer(signal);
    }

    public Signal poll() {
        return queue.poll();
    }
}