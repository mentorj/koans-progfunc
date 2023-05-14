package com.javaxpert.demos.progfunc.koans;

import io.vavr.*;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
/**
 * This class maintains a set of unit tests.
 * They all fail to pass initially, and you have to make them pass.
 * You are only allowed to change unit tests (mainly asserts fail)
 * using the koans teaching style.
 * @author deadbrain - jerome@javaxpert.com
 */
public class ProgFuncKoansTest {
    private final Function0<Integer> add2To = () -> 42;

    private static String extractSubString(String input,Integer startIndex,Integer stopIndex){
        return  input.substring(startIndex,stopIndex);
    }

    private final Function3<String,Integer,Integer,String> sameAsExtractSubString = Function3.of(ProgFuncKoansTest::extractSubString);

    private static final Function2<Integer,Integer,Integer> myIntsAdder = new Function2<Integer,Integer,Integer>(){
        @Override
        public Integer apply(Integer a,Integer b){
            return a+b;
        }
    };

    @Test
    void handlingVavrFunctionsPart1(){
        String input = "toto";
        int start=0;
        int stop=2;
        Assertions.assertEquals(extractSubString(input, start, stop), sameAsExtractSubString.apply(input, start, stop));
    }

    @Test
    void handlingVavrFunctionsPart2(){
        Assertions.assertEquals(3,myIntsAdder.apply(1,2));
    }

    @Test
    void partialFunctionsIllustratedPart1(){
        Function1<Integer,Integer> add5To = myIntsAdder.apply(5);
        Assertions.assertEquals(myIntsAdder.apply(5,3),add5To.apply(3));
    }

    @Test
    void partialFunctionsIllustratedPart2(){
        String input ="toto";
        Integer start=0;
        Integer stop = 2;
        String resultFromDirectCall = sameAsExtractSubString.apply(input,start,stop);
        Function1<Integer,String> subStringFromOUsingToto = sameAsExtractSubString.apply(input,start);
        assertThat(resultFromDirectCall).isEqualTo(subStringFromOUsingToto.apply(stop));
    }


    @Test
    void dontNeedStreamToFilterVavrCollections(){
        List<Integer> myInts=List.of(1,2,3,4,5);
        List<Integer> myOddInts= myInts.filter(integer -> integer%2!=0);
        assertThat(myOddInts).isNotEqualTo(myInts);
        assertThat(myOddInts.size()).isNotEqualTo(myInts.size());
    }
    @Test
    void vavrListsAreImmutableObjectsPart1(){
        List<Integer> myInts= List.of(1,2,3,4,5);
        List<Integer> identityList=myInts.map(integer -> integer);
        Assertions.assertNotSame(identityList, myInts);
    }
    @Test
    void vavrListsAreImmutableObjectsPart2(){
        List<Integer> myInts= List.of(1,2,3,4,5);
        List<Integer> filteredList = myInts.filter(integer -> integer%2==0);
        assertThat(filteredList).isNotEqualTo(myInts);
        assertThat(myInts.size()).isGreaterThan(filteredList.size());
    }

    @Test
    void optionIsCloseToJavaStdOptional(){
        Option<String> option = Option.of("toto");
        String value = option.getOrElse("was null");
        assertThat(value).isEqualTo("toto");
        assertThat(value).doesNotContain("null");

        Option<String> option2 = Option.of(null);
        String value2 = option2.getOrElse("else called");
        assertThat(value2).contains("called");
    }


    @Test
    void liftedFunctionsAvoidExceptionsHandling(){
        CheckedFunction0<String> callAlwaysFail  = () -> {throw new RuntimeException("failed");};
        Function0<Option<String>> lifted = CheckedFunction0.lift(callAlwaysFail);
        List<String> callingResults = List.of(1,2,3)
                .map(integer ->lifted.apply() )
                .map(option -> option.getOrElse("empty") )
                ;
        assertThat(callingResults).isEqualTo(List.of("empty","empty","empty"));
    }

    @Test
    void tryStructureIsAnAlternateToLiftedCalls(){
        CheckedFunction0<String> callAlwaysFail  = () -> {throw new RuntimeException("failed");};
        Try<String> tryCallingMethodAlwayFailing = Try.of(callAlwaysFail);
        assertThat(tryCallingMethodAlwayFailing.isFailure()).isEqualTo(true);
        String fallback = tryCallingMethodAlwayFailing.getOrElse("fallback");
        assertThat(fallback).isEqualTo("fallback");
    }

    @Test
    void tuplesAreSoHandy(){
        Tuple3<String,Integer,Integer> myTuple = Tuple.of("Hello",1,2);
        assertThat(myTuple._1).isEqualTo("Hello");
        assertThat(myTuple._1()).isEqualTo("Hello");
        assertThat(myTuple._2).isEqualTo(1);
        assertThat(myTuple._3).isEqualTo(2);
    }

    @Test
    void lazyCallsAreCachedAfterEval(){

        Lazy<Integer> lazyFunction = Lazy.of(add2To);
        assertThat(lazyFunction.isLazy()).isEqualTo(true);
        assertThat(lazyFunction.isEvaluated()).isEqualTo(false);
        Integer result = lazyFunction.get();
        assertThat(lazyFunction.isEvaluated()).isEqualTo(true);
    }

    @Test
    void memoizationCacheValuesAfterEval(){
        Function0<Double> randomized = () -> Math.random();
        Function0<Double> memoized = randomized.memoized();
        assertThat(memoized.isMemoized()).isEqualTo(true);
        double val1 = memoized.get();
        double val2 = memoized.get();
        assertThat(val1).isEqualTo(val2);
    }

    @Test
    void eitherRightMeansSucess(){
        Either<String,Integer> invokeFunctionRasingException = Either.left("KO");
        assertThat(invokeFunctionRasingException.isLeft()).isEqualTo(true);
        Either<String,Integer> computeResultOk = Either.right(42);
        assertThat(computeResultOk.isRight()).isEqualTo(true);
    }
}
