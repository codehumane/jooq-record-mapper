# jooq-record-mapper

JOOQ를 통해 얻은 DB 레코드 데이터를 POJO 객체로 변환해서 사용하고 싶을 때가 있음. 이 매핑을 돕는 도구로 [org.jooq.RecordMapper](https://www.jooq.org/javadoc/3.10.x/org/jooq/RecordMapper.html), [DefaultRecordMapper](https://www.jooq.org/javadoc/3.6.1/org/jooq/impl/DefaultRecordMapper.html), [ModelMapper](http://modelmapper.org/), [SimpleMapper](http://simpleflatmapper.org/), [OrikaMapper](http://orika-mapper.github.io/orika-docs/) 등이 있음. 그러나 몇 가지 제약들을 가짐.

1. 외부로 노출하고 싶지 않은 필드도 무조건 노출해야 하거나(public 접근 제한자 또는 getter가 필요),
2. 변환 대상이 늘어나거나 변경되거나 추가될 때 마다 매핑 코드를 함께 수정해 주어야 하거나,
3. Enum 타입에 대해 기초적인 수준만 지원하거나,
4. 심각한 버그가 있거나(이건 ModelMapper 이야기),
5. 필드명을 대응시키는 알고리즘이 너무 관대하거나, 혹은 엄격한 경우 우리 상황에 맞지 않거나,
6. 반복적인 코드를 매번 작성해주어야 하거나,
7. 부족하다면 확장할 수 있어야 하는데, 문서는 커녕 내부 코드를 이곳 저곳 뒤져봐도 그런 게 없거나.

`JooqRecordToPojoMapper`는 이 단점들을 보완한 도구.

- 보다 자세한 설명은 [여기](http://codehumane.github.io/2017/12/03/JOOQ-to-POJO-Mapping/)를 참고.
- 간단한 사용법은 [테스트 코드](https://github.com/codehumane/jooq-record-mapper/blob/master/src/test/java/codehumane/jooq/JooqRecordToPojoMapperTest.java) 참고.

## 성능 개선

- 기존에는 Reflection 연산을 사용한, O(N^2) 시간 복잡도의 코드. Reflection이 느린 이유는 [여기](https://docs.oracle.com/javase/tutorial/reflect/) 참고.
- 이 O(N^2) 연산을 한 번 수행하면 그 결과를 로컬에 캐시. 이후 변환 작업부터는 캐싱된 결과를 사용. 자세한 내용은 [CachedJooqRecordToPojoMapper](https://github.com/codehumane/jooq-record-mapper/blob/master/src/main/java/codehumane/jooq/CachedJooqRecordToPojoMapper.java) 참고. 실제 DB를 연결한 환경에서 TPS가 대략 2배 정도 향상.
