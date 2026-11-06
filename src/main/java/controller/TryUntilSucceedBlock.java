package src.main.java.controller;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TryUntilSucceedBlock<T> {
  private Supplier<T> tryBlock;
  private Consumer<Exception> exceptionBlock;

  public TryUntilSucceedBlock(Supplier<T> theTryBlock, Consumer<Exception> theExceptionBlock) {
    tryBlock = theTryBlock;
    exceptionBlock = theExceptionBlock;
  }

  public T execute() {
    T res = null;
    while(true) {
      try {
        res = tryBlock.get();
        return res;
      } catch(Exception e) {
        exceptionBlock.accept(e);
      }
    }
  }
}
