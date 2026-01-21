package ru.practicum.collector.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.server.service.GrpcService;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

import ru.practicum.collector.grpc.GrpcUserActionService;
import ru.yandex.practicum.ewm.stats.proto.UserActionProto;
import ru.yandex.practicum.grpc.stats.collector.UserActionControllerGrpc;

@GrpcService
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase  {
    private final GrpcUserActionService grpcUserActionService;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            grpcUserActionService.processUserAction(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    new StatusRuntimeException(Status.INTERNAL
                                                     .withDescription(e.getMessage())
                                                     .withCause(e))
            );
        }
    }
}
