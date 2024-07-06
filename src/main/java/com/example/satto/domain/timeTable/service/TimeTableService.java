package com.example.satto.domain.timeTable.service;

import com.example.satto.domain.currentLecture.entity.CurrentLecture;
import com.example.satto.domain.currentLecture.repository.CurrentLectureRepository;
import com.example.satto.domain.follow.repository.FollowRepository;
import com.example.satto.domain.timeTable.dto.*;
import com.example.satto.domain.timeTable.entity.TimeTable;
import com.example.satto.domain.timeTable.repository.TimeTableRepository;
import com.example.satto.domain.timeTableLecture.entity.TimeTableLecture;
import com.example.satto.domain.timeTableLecture.repository.TimeTableLectureRepository;
import com.example.satto.domain.users.entity.Users;
import com.example.satto.domain.users.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final CurrentLectureRepository currentLectureRepository;
    private final TimeTableRepository timeTableRepository;
    private final UsersRepository usersRepository;
    private final TimeTableLectureRepository timeTableLectureRepository;
    private final FollowRepository followRepository;


    //강의 시간 충돌 검사
    public static boolean isNotTimeConflict(List<TimeTableResponseDTO.TimeTableLectureDTO> lectList,
                                            TimeTableResponseDTO.TimeTableLectureDTO lect) {

        String[] lectTimeSegments = lect.lectTime().split(" "); // 한 번만 분리
        for (TimeTableResponseDTO.TimeTableLectureDTO existingLect : lectList) {
            for (String segment : lectTimeSegments) {
                if (existingLect.lectTime().contains(segment)) {
                    return false; // 충돌 발견 시 즉시 반환
                }
            }
        }
        return true; // 충돌 없음

    }
    //학수번호 충돌 검사
    public static boolean isNotLectNumberConfilct(List<TimeTableResponseDTO.TimeTableLectureDTO> lectList,
                                                  TimeTableResponseDTO.TimeTableLectureDTO lect) {

        for (TimeTableResponseDTO.TimeTableLectureDTO existingLect : lectList) {
            if (existingLect.code().equals(lect.code())) {
                return false; // 같은 강의 번호 발견 시 즉시 반환
            }
        }
        return true; // 충돌 없음
    }

    public static boolean isNotConflict(List<TimeTableResponseDTO.TimeTableLectureDTO> currentLect, TimeTableResponseDTO.TimeTableLectureDTO lect) {
        return currentLect.isEmpty() || (isNotTimeConflict(currentLect, lect) && isNotLectNumberConfilct(currentLect, lect));
    }

    //시간표 조합 알고리즘
    private void generateCombinations(List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectures,
                                      int start,
                                      List<TimeTableResponseDTO.TimeTableLectureDTO> current,
                                      List<List<TimeTableResponseDTO.TimeTableLectureDTO>> result,
                                      int targetSize
    ) {
        if (current.size() == targetSize) {
            result.add((new ArrayList<>(current)));
            return;
        }

        for (int i = start; i < majorLectures.size(); i++) {
            TimeTableResponseDTO.TimeTableLectureDTO nextMajorLecture = majorLectures.get(i);
            if (isNotConflict(current, nextMajorLecture)) {
                current.add(nextMajorLecture);
                generateCombinations(majorLectures, i + 1, current, result, targetSize);
                current.remove(current.size() - 1);
            }
        }
    }

    public List<TimeTableResponseDTO.MajorCombinationResponseDTO> createMajorTimeTable(MajorTimeTableRequestDTO createDTO, Users users) {

        //3학년 1학기 4전공 선택 기준
        List<CurrentLecture> majorLectList = currentLectureRepository.findCurrentLectureByDepartmentAndGrade(users.getDepartment(), users.getGrade());
        List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(majorLectList);

        List<TimeTableResponseDTO.TimeTableLectureDTO> lectDetailList = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> timeTable = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> requiredTimeTable = new ArrayList<>();
        List<TimeTableResponseDTO.EntireTimeTableResponseDTO> result;

        List<CurrentLecture> requiredLectList = new ArrayList<>();
        List<TimeTableResponseDTO.TimeTableLectureDTO> requiredLectDetailList = new ArrayList<>();
        majorLectDetailList = removeLecturesInImpossibleTimeZones(majorLectDetailList, createDTO.impossibleTimeZone());

        int count = createDTO.majorCount();

        if(!createDTO.requiredLect().get(0).isEmpty()) {

            for (String lectCodeSection : createDTO.requiredLect()) {

                CurrentLecture requiredLect = currentLectureRepository.findCurrentLectureByCodeSection(lectCodeSection);
                requiredLectList.add(requiredLect);
                if(!requiredLect.getCmpDiv().equals("1전심") && !requiredLect.getCmpDiv().equals("1전선")) {count++;}

            }

            requiredLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(requiredLectList);

            generateCombinations(requiredLectDetailList, 0, lectDetailList, requiredTimeTable, requiredLectDetailList.size());

            for(List<TimeTableResponseDTO.TimeTableLectureDTO> lect : requiredTimeTable){
                generateCombinations(majorLectDetailList, 0, lect, timeTable, count);
            }
        }
        else {
            generateCombinations(majorLectDetailList, 0, lectDetailList, timeTable, createDTO.majorCount());
        }

        result = TimeTableResponseDTO.EntireTimeTableResponseDTO.fromList(timeTable);

        List<Integer> toRemoveIndexes = new ArrayList<>();

        for( int i = 0; i < result.size(); i++){
            if(result.get(i).timeTable().size() == 0) {toRemoveIndexes.add(i);}
        }
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            result.remove(index);
        }

        return calculateMajorCombinations(result);
    }

    //시간대 벤 로직
    public List<TimeTableResponseDTO.TimeTableLectureDTO> removeLecturesInImpossibleTimeZones(List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectDetailList, String impossibleTimeZones) {

        if (impossibleTimeZones == null || impossibleTimeZones.trim().isEmpty()) {
            return majorLectDetailList;
        }

        String[] timeSegments = impossibleTimeZones.split(" ");
        Set<String> impossibleTimeSet = new HashSet<>(Arrays.asList(timeSegments));
        List<TimeTableResponseDTO.TimeTableLectureDTO> filteredList = new ArrayList<>();

        for (TimeTableResponseDTO.TimeTableLectureDTO lecture : majorLectDetailList) {
            boolean isPossible = true;
            String[] lectureTimes = lecture.lectTime().split(" ");
            for (String time : lectureTimes) {
                if (impossibleTimeSet.contains(time)) {
                    isPossible = false;
                    break;
                }
            }
            if (isPossible) {
                filteredList.add(lecture);
            }
        }

        return filteredList;
    }

    //전공 조합의 개수 계산
    public List<TimeTableResponseDTO.MajorCombinationResponseDTO> calculateMajorCombinations(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> result) {
        Set<Set<TimeTableResponseDTO.LectureCombination>> uniqueCombinations = new HashSet<>();

        for (TimeTableResponseDTO.EntireTimeTableResponseDTO data : result) {
            Set<TimeTableResponseDTO.LectureCombination> sbjCombinations = data.timeTable().stream()
                    .filter(lecture -> "1전심".equals(lecture.cmpDiv()) || "1전선".equals(lecture.cmpDiv()))
                    .map(lecture -> new TimeTableResponseDTO.LectureCombination(lecture.lectName(), lecture.code()))
                    .collect(Collectors.toSet());

            uniqueCombinations.add(sbjCombinations);
        }

        // 중복되지 않는 조합만 리스트로 변환
        return uniqueCombinations.stream()
                .map(sbjCombinations -> new TimeTableResponseDTO.MajorCombinationResponseDTO(sbjCombinations))
                .collect(Collectors.toList());
    }


    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> createTimeTable(EntireTimeTableRequestDTO createDTO) {

        List<CurrentLecture> entireLect = currentLectureRepository.findLectByCmpDiv("교선");
        List<TimeTableResponseDTO.TimeTableLectureDTO> entireLectList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(entireLect);

        Set<String> majorSet = new HashSet<>();
        List<CurrentLecture> majorList = new ArrayList<>();

        for (List<String> majors : createDTO.majorList()){
            majorSet.addAll(majors);
        }

        for (String majorName : majorSet){
            majorList.addAll(currentLectureRepository.findCurrentLecturesByCode(majorName));
        }

        List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(majorList);


        List<TimeTableResponseDTO.TimeTableLectureDTO> lectDetailList = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> requiredTimeTable = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> majorTimeTable = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> timeTable = new ArrayList<>();
        List<TimeTableResponseDTO.EntireTimeTableResponseDTO> result;

        majorLectDetailList = removeLecturesInImpossibleTimeZones(majorLectDetailList, createDTO.impossibleTimeZone());
        entireLectList = removeLecturesInImpossibleTimeZones(entireLectList, createDTO.impossibleTimeZone());

        //임의 교양 리스트 생성
//        Collections.shuffle(entireLectList);
//        List<CurrentLectureResponseDTO> randomList = entireLectList.subList(0,10);
        List<CurrentLecture> requiredLectList = new ArrayList<>();
        List<TimeTableResponseDTO.TimeTableLectureDTO> requiredLectDetailList = new ArrayList<>();

        int count = createDTO.majorCount();

        if(!createDTO.requiredLect().get(0).isEmpty()) {

            for (String lectCodeSection : createDTO.requiredLect()) {

                CurrentLecture requiredLect = currentLectureRepository.findCurrentLectureByCodeSection(lectCodeSection);
                requiredLectList.add(requiredLect);
                if(!requiredLect.getCmpDiv().equals("1전심") && !requiredLect.getCmpDiv().equals("1전선")) {count++;}

            }

            requiredLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(requiredLectList);

            generateCombinations(requiredLectDetailList, 0, lectDetailList, requiredTimeTable, requiredLectDetailList.size());

            for(List<TimeTableResponseDTO.TimeTableLectureDTO> lect : requiredTimeTable){
                generateCombinations(majorLectDetailList, 0, lect, majorTimeTable, count);
            }
        }
        else {
            generateCombinations(majorLectDetailList, 0, lectDetailList, majorTimeTable, createDTO.majorCount());
        }

        for(List<TimeTableResponseDTO.TimeTableLectureDTO> lect : majorTimeTable){
            generateCombinations1212(entireLectList, 0, lect, timeTable, createDTO.GPA()-createDTO.cyberCount()*3);
        }

        result = TimeTableResponseDTO.EntireTimeTableResponseDTO.fromList(timeTable);
        optimizationTimeTable(result);
        optimizationTimeTable2(result);
        optimizationTimeTable3(result);
        optimizationTimeTable4(result);
//        optimizationTimeTable5(result);
//        optimizationTimeTable6(result);

        return  result;
    }
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> createTimeTableRaw(EntireTimeTableRequestDTO createDTO) {

        List<CurrentLecture> entireLect = currentLectureRepository.findLectByCmpDiv("교선");
        List<TimeTableResponseDTO.TimeTableLectureDTO> entireLectList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(entireLect);

        Set<String> majorSet = new HashSet<>();
        List<CurrentLecture> majorList = new ArrayList<>();

        for (List<String> majors : createDTO.majorList()){
            majorSet.addAll(majors);
        }

        for (String majorName : majorSet){
            majorList.addAll(currentLectureRepository.findCurrentLecturesByCode(majorName));
        }

        List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(majorList);


        List<TimeTableResponseDTO.TimeTableLectureDTO> lectDetailList = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> requiredTimeTable = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> majorTimeTable = new ArrayList<>();
        List<List<TimeTableResponseDTO.TimeTableLectureDTO>> timeTable = new ArrayList<>();
        List<TimeTableResponseDTO.EntireTimeTableResponseDTO> result;

        majorLectDetailList = removeLecturesInImpossibleTimeZones(majorLectDetailList, createDTO.impossibleTimeZone());
        entireLectList = removeLecturesInImpossibleTimeZones(entireLectList, createDTO.impossibleTimeZone());

        //임의 교양 리스트 생성
//        Collections.shuffle(entireLectList);
//        List<CurrentLectureResponseDTO> randomList = entireLectList.subList(0,10);
        List<CurrentLecture> requiredLectList = new ArrayList<>();
        List<TimeTableResponseDTO.TimeTableLectureDTO> requiredLectDetailList = new ArrayList<>();

        int count = createDTO.majorCount();

        if(!createDTO.requiredLect().get(0).isEmpty()) {

            for (String lectCodeSection : createDTO.requiredLect()) {

                CurrentLecture requiredLect = currentLectureRepository.findCurrentLectureByCodeSection(lectCodeSection);
                requiredLectList.add(requiredLect);
                if(!requiredLect.getCmpDiv().equals("1전심") && !requiredLect.getCmpDiv().equals("1전선")) {count++;}

            }

            requiredLectDetailList = TimeTableResponseDTO.TimeTableLectureDTO.fromList(requiredLectList);

            generateCombinations(requiredLectDetailList, 0, lectDetailList, requiredTimeTable, requiredLectDetailList.size());

            for(List<TimeTableResponseDTO.TimeTableLectureDTO> lect : requiredTimeTable){
                generateCombinations(majorLectDetailList, 0, lect, majorTimeTable, count);
            }
        }
        else {
            generateCombinations(majorLectDetailList, 0, lectDetailList, majorTimeTable, createDTO.majorCount());
        }

        for(List<TimeTableResponseDTO.TimeTableLectureDTO> lect : majorTimeTable){
            generateCombinations1212(entireLectList, 0, lect, timeTable, createDTO.GPA()-createDTO.cyberCount()*3);
        }

        result = TimeTableResponseDTO.EntireTimeTableResponseDTO.fromList(timeTable);
//        optimizationTimeTable(result);
//        optimizationTimeTable2(result);
//        optimizationTimeTable3(result);
//        optimizationTimeTable4(result);
//        optimizationTimeTable5(result);
//        optimizationTimeTable6(result);

        return  result;
    }

    public Long createTimeTable(TimeTableSelectRequestDTO selectRequestDTO, Users users){

        List<TimeTable> timeTables = timeTableRepository.findAllByUserId(users.getUserId());

        if (selectRequestDTO.isRepresented()) {
            for (TimeTable tt : timeTables) {
                if (tt.getSemesterYear().equals(selectRequestDTO.semesterYear()) && tt.getIsRepresented()) {
                    throw new IllegalStateException("이미 해당 학기의 대표 시간표가 존재합니다.");
                }
            }
        }
        TimeTable timeTable = timeTableRepository.save(selectRequestDTO.to(users));
        return timeTable.getTimetableId();
    }

    public TimeTableResponseDTO.SelectTimeTableResponseDTO getRepresentTimeTable(Users users) {
        // 가장 최근 생성된 대표 시간표를 가져옴
        TimeTable timeTable = timeTableRepository.findLatestRepresentedTimeTableByUserId(users.getUserId());
        if (timeTable == null) {
            throw new IllegalStateException("대표 시간표가 존재하지 않습니다.");
        }

        List<TimeTableLecture> lectures = timeTableLectureRepository.findTimeTableLecturesByTimeTableId(timeTable.getTimetableId());
        List<CurrentLecture> currentLectures = new ArrayList<>();
        for (TimeTableLecture t : lectures) {
            currentLectures.add(t.getCurrentLecture());
        }

        List<TimeTableResponseDTO.TimeTableLectureDTO> timeTableLectures = TimeTableResponseDTO.TimeTableLectureDTO.fromList(currentLectures);
        return TimeTableResponseDTO.SelectTimeTableResponseDTO.from(timeTableLectures, timeTable);
    }

    public List<TimeTableResponseDTO.timeTableListDTO> getTimeTableList(Users users){
        List<TimeTable> timeTables = timeTableRepository.findAllByUserId(users.getUserId());
        return TimeTableResponseDTO.timeTableListDTO.fromList(timeTables);
    }

    public TimeTableResponseDTO.SelectTimeTableResponseDTO getTimeTable(Long timeTableId){
        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        List<TimeTableLecture> lectures = timeTableLectureRepository.findTimeTableLecturesByTimeTableId(timeTableId);
        List<CurrentLecture> currentLectures = new ArrayList<>();
        for( TimeTableLecture t : lectures ){
            currentLectures.add(t.getCurrentLecture());
        }
        List<TimeTableResponseDTO.TimeTableLectureDTO> timeTableLectures = TimeTableResponseDTO.TimeTableLectureDTO.fromList(currentLectures);
        return TimeTableResponseDTO.SelectTimeTableResponseDTO.from(timeTableLectures,timeTable);
    }


    public List<TimeTableResponseDTO.timeTableListDTO> getOtherTimeTableList(String studentId,Users users){

        Users target = usersRepository.findByStudentId(studentId).orElseThrow();
        List<TimeTable> timeTables = new ArrayList<>();

        if(!target.isPublic()){
            if(!followRepository.existsByFollowerIdStudentIdAndFollowingIdStudentId(users.getStudentId(), studentId)){
                throw new IllegalStateException("볼 수 있는 시간표가 존재하지 않습니다.");
            }
        }
        timeTables = timeTableRepository.findPublicTimeTableByStudentId(studentId);

        return TimeTableResponseDTO.timeTableListDTO.fromList(timeTables);
    }

    public void updateTimeTableIsPublic(Long timeTableId, UpdateTimeTableRequestDTO isPublic){

        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        timeTable.updateIsPublic(isPublic.state());
        timeTableRepository.save(timeTable);
    }

    public void updateTimeTableIsRepresented(Long timeTableId, UpdateTimeTableRequestDTO isRepresented, Users users){
        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        List<TimeTable> timeTables = timeTableRepository.findAllByUserId(users.getUserId());

        if (isRepresented.state()) {
            for (TimeTable tt : timeTables) {
                if (tt.getSemesterYear().equals(timeTable.getSemesterYear()) && tt.getIsRepresented()) {
                    tt.updateIsRepresented(false);
                }
            }
        }

        timeTable.updateIsRepresented(isRepresented.state());
        timeTableRepository.save(timeTable);
    }

    public void updateTimeTableName(Long timeTableId, UpdateTimeTableNameRequestDTO timeTableName){
        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        timeTable.updateName(timeTableName.timeTableName());
        timeTableRepository.save(timeTable);
    }

    public void deleteTimeTable(Long timeTableId){
        List<TimeTableLecture> timeTableLectures = timeTableLectureRepository.findTimeTableLecturesByTimeTableId(timeTableId);
        TimeTable timeTable = timeTableRepository.findById(timeTableId).orElseThrow();
        timeTableLectureRepository.deleteAll(timeTableLectures);
        timeTableRepository.delete(timeTable);
    }

    //시간표 조합 알고리즘
    private void generateCombinations1212(List<TimeTableResponseDTO.TimeTableLectureDTO> majorLectures,
                                          int start,
                                          List<TimeTableResponseDTO.TimeTableLectureDTO> current,
                                          List<List<TimeTableResponseDTO.TimeTableLectureDTO>> result,
                                          int targetSize
    ) {
        int currentCredits = current.stream().mapToInt(lecture -> lecture.credit()).sum();
        if ((currentCredits == targetSize) && !current.isEmpty()) {
            result.add((new ArrayList<>(current)));
            return;
        }

        for (int i = start; i < majorLectures.size(); i++) {
            TimeTableResponseDTO.TimeTableLectureDTO nextMajorLecture = majorLectures.get(i);
            int nextCredits = currentCredits + nextMajorLecture.credit();
            if (nextCredits <= targetSize && isNotConflict(current, nextMajorLecture)) {
                current.add(nextMajorLecture);
                generateCombinations1212(majorLectures, i + 1, current, result, targetSize);
                current.remove(current.size() - 1);
            }
        }
    }
    //공강이 3시간 이상 존재하는 경우 제거
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables){

        List<Integer> toRemoveIndexes = new ArrayList<>();
        for(int i = 0; i < timeTables.size(); i++){

            Map<Character, TreeSet<Integer>> scheduleMap = new HashMap<>();
            String[] parts = timeTables.get(i).totalTime().split(" ");
            for (String part : parts) {
                char day = part.charAt(0); // 요일
                int time = Integer.parseInt(part.substring(1)); // 시간

                // 해당 요일에 대한 TreeSet이 없으면 생성
                scheduleMap.putIfAbsent(day, new TreeSet<>());
                // 시간 추가
                scheduleMap.get(day).add(time);
            }

            boolean hasThreeHourGap = false;
            // 각 요일별로 시간대 확인
            for (TreeSet<Integer> times : scheduleMap.values()) {
                // 이전 시간대 저장
                Integer prevTime = null;
                for (Integer currentTime : times) {
                    if (prevTime != null && (currentTime - prevTime > 3)) {
                        // 3교시 이상 비어있으면 다음 시간표로 넘어감
                        hasThreeHourGap = true;
                        break;
                    }
                    prevTime = currentTime;
                }
                if (!hasThreeHourGap) {
                    break; // 현재 시간표에서 3교시 이상 비어있지 않는 부분을 찾았으면 더 이상 확인하지 않음
                }
            }
            if (hasThreeHourGap) {
                // 3교시 이상 비어있는 경우 리스트에서 제거
                toRemoveIndexes.add(i);
            }
        }
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

    //하루에 강의가 1시간만 있는 경우 제거
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable2(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables) {
        // 제거하기 위한 인덱스 리스트
        List<Integer> toRemoveIndexes = new ArrayList<>();

        for (int i = 0; i < timeTables.size(); i++) {
            Map<Character, TreeSet<Integer>> scheduleMap = new HashMap<>();
            String[] parts = timeTables.get(i).totalTime().split(" ");
            for (String part : parts) {
                char day = part.charAt(0); // 요일
                int time = Integer.parseInt(part.substring(1)); // 시간

                // 해당 요일에 대한 TreeSet 생성 또는 시간 추가
                scheduleMap.putIfAbsent(day, new TreeSet<>());
                scheduleMap.get(day).add(time);
            }

            // 하루에 강의가 1시간만 있는 경우를 찾기
            boolean hasOneHourClassOnly = scheduleMap.values().stream()
                    .anyMatch(times -> times.size() == 1); // 하루에 시간이 정확히 1개만 있는 경우 확인

            if (hasOneHourClassOnly) {
                // 하루에 강의가 1시간만 있는 경우 인덱스 저장
                toRemoveIndexes.add(i);
            }
        }

        // 뒤에서부터 제거하여 인덱스 변화를 방지
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

    //공강일이 없는 경우 제거
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable3(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables) {
        // 제거하기 위한 인덱스 리스트
        List<Integer> toRemoveIndexes = new ArrayList<>();

        for (int i = 0; i < timeTables.size(); i++) {
            Map<Character, TreeSet<Integer>> scheduleMap = new HashMap<>();
            String[] parts = timeTables.get(i).totalTime().split(" ");
            for (String part : parts) {
                char day = part.charAt(0); // 요일
                int time = Integer.parseInt(part.substring(1)); // 시간

                // 해당 요일에 대한 TreeSet 생성 또는 시간 추가
                scheduleMap.putIfAbsent(day, new TreeSet<>());
                scheduleMap.get(day).add(time);
            }

            // 모든 요일에 강의가 있는지 확인
            boolean hasNoEmptyDay = scheduleMap.keySet().size() == 5; // 월화수목금을 기준으로 5일 모두 강의가 있는지 확인

            if (hasNoEmptyDay) {
                // 모든 요일에 강의가 있어 공강이 없는 경우 인덱스 저장
                toRemoveIndexes.add(i);
            }
        }

        // 뒤에서부터 제거하여 인덱스 변화를 방지
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

    //7시간 이상 연속으로 강의가 존재하는 시간표 제외
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable4(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables){

        List<Integer> toRemoveIndexes = new ArrayList<>();
        for(int i = 0; i < timeTables.size(); i++){

            Map<Character, TreeSet<Integer>> scheduleMap = new HashMap<>();
            String[] parts = timeTables.get(i).totalTime().split(" ");
            for (String part : parts) {
                char day = part.charAt(0); // 요일
                int time = Integer.parseInt(part.substring(1)); // 시간

                // 해당 요일에 대한 TreeSet이 없으면 생성
                scheduleMap.putIfAbsent(day, new TreeSet<>());
                // 시간 추가
                scheduleMap.get(day).add(time);
            }

            boolean hasSevenHourContinuous = false;
            // 각 요일별로 시간대 확인
            for (TreeSet<Integer> times : scheduleMap.values()) {
                // 연속 시간 확인
                Integer firstTime = null;
                Integer lastTime = null;
                for (Integer currentTime : times) {
                    if (firstTime == null) {
                        firstTime = currentTime;
                        lastTime = currentTime;
                    } else if (currentTime - lastTime == 1) {
                        lastTime = currentTime;
                        // 연속되는 시간이 7시간 이상인지 확인
                        if (lastTime - firstTime >= 6) {
                            hasSevenHourContinuous = true;
                            break;
                        }
                    } else {
                        firstTime = currentTime;
                        lastTime = currentTime;
                    }
                }
                if (hasSevenHourContinuous) {
                    break; // 7시간 이상 연속되는 경우 찾았으면 더 이상 확인하지 않음
                }
            }
            if (hasSevenHourContinuous) {
                // 7시간 이상 연속된 경우 리스트에서 제거
                toRemoveIndexes.add(i);
            }
        }
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

    //0교시, 1교시, 2교시 강의가 존재하는 시간표 제외
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable5(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables) {

        List<Integer> toRemoveIndexes = new ArrayList<>();
        for (int i = 0; i < timeTables.size(); i++) {
            if (timeTables.get(i).totalTime().contains("1")) {
                toRemoveIndexes.add(i);
            } else if (timeTables.get(i).totalTime().contains("2")) {
                toRemoveIndexes.add(i);
            }
        }

        // 뒤에서부터 제거하여 인덱스 변화를 방지
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

    //9시~11시 중 강의가 있고 12시 이후에도 강의가 있는 경우 점심시간(11시~13시중 1시간)이 확보되지 않은 시간표 제외
    public List<TimeTableResponseDTO.EntireTimeTableResponseDTO> optimizationTimeTable6(List<TimeTableResponseDTO.EntireTimeTableResponseDTO> timeTables){

        List<Integer> toRemoveIndexes = new ArrayList<>();
        for(int i = 0; i < timeTables.size(); i++){

            Map<Character, TreeSet<Integer>> scheduleMap = new HashMap<>();
            String[] parts = timeTables.get(i).totalTime().split(" ");
            for (String part : parts) {
                char day = part.charAt(0); // 요일
                int time = Integer.parseInt(part.substring(1)); // 시간

                // 해당 요일에 대한 TreeSet이 없으면 생성
                scheduleMap.putIfAbsent(day, new TreeSet<>());
                // 시간 추가
                scheduleMap.get(day).add(time);
            }

            boolean isValidTimeTable = false;
            for (TreeSet<Integer> times : scheduleMap.values()) {
                // 1~3교시 중 강의가 존재하는지 확인
                boolean hasEarlyClass = times.stream().anyMatch(time -> time >= 1 && time <= 3);
                // 4교시 이후 강의가 존재하는지 확인
                boolean hasLateClass = times.stream().anyMatch(time -> time >= 4);

                if (hasEarlyClass && hasLateClass) {
                    // 3교시, 4교시, 5교시 중 비어있는 시간이 1시간 이상 있는지 확인
                    boolean hasGap = false;
                    for (int j = 3; j <= 5; j++) {
                        if (!times.contains(j)) {
                            hasGap = true;
                            break;
                        }
                    }
                    // 조건을 만족하지 않는 시간표는 제외
                    if (!hasGap) {
                        isValidTimeTable = true;
                        break;
                    }
                }
            }

            if (isValidTimeTable) {
                toRemoveIndexes.add(i);
            }
        }
        Collections.reverse(toRemoveIndexes);
        for (int index : toRemoveIndexes) {
            timeTables.remove(index);
        }

        return timeTables;
    }

}
