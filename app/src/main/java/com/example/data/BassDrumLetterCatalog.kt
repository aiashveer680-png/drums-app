package com.example.data

import com.example.model.BassDrumLetter
import com.example.model.BassDrumLetterCategory

object BassDrumLetterCatalog {

    val allLetters: List<BassDrumLetter> = listOf(
        // ================= SINGLE NOTES (A - D) =================
        BassDrumLetter(
            letter = "A",
            pattern = "1000",
            category = BassDrumLetterCategory.SINGLE_NOTE,
            description = "Kick plays on the downbeat '1'. As printed on page 18 of The Language of Drumming, kick plays on beats 1 and 3 while the snare beats on 2 and 4 stay open.",
            coachTip = "Focus on solid alignment between your right foot on the kick and your right hand on the hi-hat. Ensure the bass drum impact is completely locked with beat 1."
        ),
        BassDrumLetter(
            letter = "B",
            pattern = "0100",
            category = BassDrumLetterCategory.SINGLE_NOTE,
            description = "Kick plays on the 16th-note 'e' of every beat. The first upbeat subdivision.",
            coachTip = "This is the classic delayed funk kick! Your hands play eighth notes on the hi-hat while your foot slips in right between the hi-hat eighths."
        ),
        BassDrumLetter(
            letter = "C",
            pattern = "0010",
            category = BassDrumLetterCategory.SINGLE_NOTE,
            description = "Kick plays on the '&' (and) upbeat of every beat. Unison with the second hi-hat stroke.",
            coachTip = "Your kick hits simultaneously with the second eighth note of each beat. Avoid rushing ahead of the hi-hat."
        ),
        BassDrumLetter(
            letter = "D",
            pattern = "0001",
            category = BassDrumLetterCategory.SINGLE_NOTE,
            description = "Kick plays on the 'a' of every beat (the last 16th note right before the next downbeat).",
            coachTip = "The push note! Think of the kick as an anticipation leading into the next beat. Keep the tempo relaxed so it doesn't drag or rush."
        ),

        // ================= TWO NOTES (E - J) =================
        BassDrumLetter(
            letter = "E",
            pattern = "1100",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on '1' and 'e' of every beat. Two quick successive 16th notes starting on the downbeat.",
            coachTip = "Master the double pedal slide or swivel technique. The second note must have equal punch and projection as the first note."
        ),
        BassDrumLetter(
            letter = "F",
            pattern = "0110",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on 'e' and '&' of every beat. A double stroke centered on the middle of the beat.",
            coachTip = "Sing 'chid - e - and' mentally. The foot enters right after the downbeat and resolves onto the '&' upbeat."
        ),
        BassDrumLetter(
            letter = "G",
            pattern = "0011",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on '&' and 'a' of every beat. A double stroke leading right up to the next downbeat.",
            coachTip = "A staple in hip-hop, modern pop, and R&B grooves. Anchor the hi-hat steady while your foot drives into the backbeat."
        ),
        BassDrumLetter(
            letter = "H",
            pattern = "1001",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on '1' and 'a' of every beat. Brackets the outer edges of the beat.",
            coachTip = "One kick on the downbeat, space in the middle, and one kick right before the next beat. Very funky syncopation!"
        ),
        BassDrumLetter(
            letter = "I",
            pattern = "1010",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on '1' and '&' of every beat. Constant straight eighth notes on the bass drum.",
            coachTip = "Straight eighth-note kick pulse. Locks perfectly with your right hand on the hi-hat. Great for driving four-on-the-floor energy."
        ),
        BassDrumLetter(
            letter = "J",
            pattern = "0101",
            category = BassDrumLetterCategory.TWO_NOTE,
            description = "Kick plays on 'e' and 'a' of every beat. The two syncopated 16th notes surrounding the upbeat.",
            coachTip = "High-level foot coordination! The kick plays the two subdivisions where the hi-hat does NOT play. Pure linear independence."
        ),

        // ================= THREE NOTES (K - N) =================
        BassDrumLetter(
            letter = "K",
            pattern = "1110",
            category = BassDrumLetterCategory.THREE_NOTE,
            description = "Kick plays on '1', 'e', and '&' of every beat. Three consecutive 16th notes with a rest on 'a'.",
            coachTip = "Three-note burst starting on the beat. Keep the ankle loose and let the pedal spring bounce naturally without tensing."
        ),
        BassDrumLetter(
            letter = "L",
            pattern = "0111",
            category = BassDrumLetterCategory.THREE_NOTE,
            description = "Kick plays on 'e', '&', and 'a' of every beat. Three consecutive 16th notes with a rest on the downbeat.",
            coachTip = "Wait for the downbeat '1', then play the three-note burst! Vocalize 'chid' on the downbeat to keep your hands grounded."
        ),
        BassDrumLetter(
            letter = "M",
            pattern = "1011",
            category = BassDrumLetterCategory.THREE_NOTE,
            description = "Kick plays on '1', '&', and 'a' of every beat. Space on the 'e' subdivision.",
            coachTip = "One downbeat kick, followed by an eighth-note feel, and a quick double into the next beat. Very common in Latin and fusion grooves."
        ),
        BassDrumLetter(
            letter = "N",
            pattern = "1101",
            category = BassDrumLetterCategory.THREE_NOTE,
            description = "Kick plays on '1', 'e', and 'a' of every beat. Space on the '&' upbeat.",
            coachTip = "Two quick kicks on the front of the beat, then a kick on the tail. Watch out for your snare on beats 2 and 4."
        ),

        // ================= FULL & REST (O & P) =================
        BassDrumLetter(
            letter = "O",
            pattern = "1111",
            category = BassDrumLetterCategory.FULL_AND_REST,
            description = "Kick plays on all four 16th notes ('1', 'e', '&', 'a') of every beat. Continuous 16th-note stream.",
            coachTip = "Continuous foot motion. Tests true independence between your steady hi-hat eighths, snare backbeats on 2 and 4, and rolling kick foot."
        ),
        BassDrumLetter(
            letter = "P",
            pattern = "0000",
            category = BassDrumLetterCategory.FULL_AND_REST,
            description = "Letter P is a complete rest for the bass drum. Hands only play the groove (hi-hat eighths, snare on 2 and 4).",
            coachTip = "The control test: can you keep your foot perfectly still and relaxed while your hands groove without losing time? Say 'chid' on each beat."
        )
    )

    fun getByLetter(letter: String): BassDrumLetter? = allLetters.find { it.letter.equals(letter, ignoreCase = true) }
}
