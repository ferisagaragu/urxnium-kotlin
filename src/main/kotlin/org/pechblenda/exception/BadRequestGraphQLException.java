package org.pechblenda.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class BadRequestGraphQLException extends RuntimeException implements GraphQLError {

  public BadRequestGraphQLException(String message) {
    super(message);
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return null;
  }

  @Override
  public StackTraceElement[] getStackTrace() {
    return new StackTraceElement[] { };
  }
}