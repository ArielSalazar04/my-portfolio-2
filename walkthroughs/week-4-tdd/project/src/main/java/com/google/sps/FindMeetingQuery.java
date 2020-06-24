// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Set;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        // Will contain all time ranges that are unavailable for the requested meeting
        ArrayList<TimeRange> unavailableTimeRanges = new ArrayList<>();

        // Set of attendess for requested meeting
        Collection<String> requestAttendees = request.getAttendees();
        
        // Time range prior to the time range of the nth event in the for loop below
        TimeRange previousTimeRange = null;

        // Requested meeting with a duration of more than a day is impossible
        if (request.getDuration() > 1440)
            return new ArrayList<>();
        
        // For loop adds the meeting time ranges to unavailable time range ArrayList such that at least one member in the event is also a
        // member in the requested meeting. Overlapping time ranges will be combined. 
        for (Event singleEvent : events){
            TimeRange nextTimeRange = singleEvent.getWhen();
            Set<String> eventAttendees = singleEvent.getAttendees();

            // If no member from the event is also a member in the requested meeting, then the meeting time is considered available time
            if (!hasParticipant(requestAttendees, eventAttendees))
                continue;

            // previousTimeRange will be null in the first interation, and will be used to access the methods of the TimeRange class
            if (previousTimeRange == null)
                previousTimeRange = nextTimeRange.fromStartDuration(0, 0);

            // Add new time range to ArrayList
            if (!nextTimeRange.contains(previousTimeRange) && nextTimeRange.start() - previousTimeRange.end() >= request.getDuration()){
                unavailableTimeRanges.add(nextTimeRange);
                previousTimeRange = nextTimeRange;
            }

            // Update the last time range in ArrayList as the combination of that same time range with the current time range
            else{
                int start = Math.min(previousTimeRange.start(), nextTimeRange.start());
                int end = Math.max(previousTimeRange.end(), nextTimeRange.end());

                TimeRange mergedTimeRange = previousTimeRange.fromStartEnd(start, end, false);
                previousTimeRange = mergedTimeRange;

                if (!unavailableTimeRanges.isEmpty())
                    unavailableTimeRanges.remove(unavailableTimeRanges.size() - 1);
                unavailableTimeRanges.add(mergedTimeRange);
            }
        }

        // WIll contain all the free time ranges
        ArrayList<TimeRange> freeTimeRanges = new ArrayList<>();
        int start = 0;

        // All gaps between unavailable time ranges will be considered available time ranges for requested meeting
        for (TimeRange singleTimeRange : unavailableTimeRanges){
            int end = singleTimeRange.start();
            if (end - start >= request.getDuration())
                freeTimeRanges.add(previousTimeRange.fromStartEnd(start, end, false));
            start = singleTimeRange.end();
        }

        if (previousTimeRange.END_OF_DAY - start >= request.getDuration())
            freeTimeRanges.add(previousTimeRange.fromStartEnd(start, previousTimeRange.END_OF_DAY, true));

        return freeTimeRanges;
    }

    // Check if at least one member in event is also in requested meeting
    public boolean hasParticipant(Collection<String> requestAttendees, Set<String> eventAttendees){
        return !Collections.disjoint(requestAttendees,eventAttendees);
    }
}
